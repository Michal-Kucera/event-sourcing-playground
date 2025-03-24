package com.michal.merchant.usecase.merchants.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.michal.jooq.public.tables.records.MerchantProjectionRecord
import com.michal.jooq.public.tables.references.MERCHANT_PROJECTION
import com.michal.merchant.domain.event.MerchantEvent
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataReconciled
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ReplayStatus
import org.axonframework.eventhandling.SequenceNumber
import org.jooq.DSLContext
import org.jooq.JSONB
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@ProcessingGroup("merchants")
class MerchantReadModelProjector(
    private val jooqContext: DSLContext,
    private val objectMapper: ObjectMapper,
    @Value("\${axon.eventhandling.processors.merchants.simulate-failure}")
    private val shouldFail: Boolean
) {

    @EventHandler
    @Transactional(propagation = REQUIRES_NEW)
    fun on(
        event: MerchantEvent,
        @SequenceNumber sequenceNumber: Long,
        replayStatus: ReplayStatus
    ) {
        println(
            "Applying ${event.javaClass.simpleName} event to merchant ${event.aggregateId} projection " +
                    "in version $sequenceNumber"
        )
        when (event) {
            is MerchantOnboarded -> on(event, sequenceNumber, replayStatus)
            is PiiDataSubmitted -> on(event, sequenceNumber)
            is PiiDataReconciled -> on(event, sequenceNumber)
        }
        if (shouldFail) {
            error("🤷🏼‍♂️ Oh no! Something has gone sideways with merchant ${event.aggregateId}!")
        }
    }

    private fun on(event: MerchantOnboarded, sequenceNumber: Long, replayStatus: ReplayStatus) {
        if (replayStatus.isReplay) {
            jooqContext.deleteFrom(MERCHANT_PROJECTION)
                .where(MERCHANT_PROJECTION.MERCHANT_ID.eq(event.aggregateId.value))
                .execute()
        }
        jooqContext.insertInto(MERCHANT_PROJECTION)
            .set(MerchantProjectionRecord().apply {
                merchantId = event.aggregateId.value
                data = JSONB.jsonb(
                    objectMapper.writeValueAsString(
                        Projection(
                            id = event.aggregateId.value,
                            name = null,
                            platformId = event.platformId.platformId,
                            externalId = event.platformId.merchantExternalId,
                            countryCode = event.legalAddress.country.code,
                            postCode = event.legalAddress.postCode,
                            city = event.legalAddress.city,
                            addressLine1 = event.legalAddress.addressLine1,
                            addressLine2 = event.legalAddress.addressLine2,
                            currencyCode = event.currency.code.currencyCode,
                            kitchenTypes = event.kitchenTypes.map { it.value }.toSet(),
                            vatNumber = null,
                            registrationNumber = null,
                            latestSubmittedPiiDataVersion = 0,
                            latestReconciledPiiDataVersion = 0,
                        )
                    )
                )
                latestSequenceNumber = sequenceNumber
            })
            .execute()
    }

    private fun on(event: PiiDataReconciled, sequenceNumber: Long) {
        val projection = jooqContext.selectFrom(MERCHANT_PROJECTION)
            .where(
                MERCHANT_PROJECTION.MERCHANT_ID.eq(event.aggregateId.value),
                MERCHANT_PROJECTION.LATEST_SEQUENCE_NUMBER.eq(sequenceNumber - 1)
            )
            .single()
        val projectionData = objectMapper.readValue(projection.data!!.data(), Projection::class.java)
        projection.data = JSONB.jsonb(
            objectMapper.writeValueAsString(
                projectionData.copy(
                    name = event.reconciledName.value,
                    countryCode = event.reconciledLegalAddress.country.code,
                    postCode = event.reconciledLegalAddress.postCode,
                    city = event.reconciledLegalAddress.city,
                    addressLine1 = event.reconciledLegalAddress.addressLine1,
                    addressLine2 = event.reconciledLegalAddress.addressLine2,
                    vatNumber = event.reconciledLegalEntityId.vatNumber,
                    registrationNumber = event.reconciledLegalEntityId.registrationNumber,
                    latestReconciledPiiDataVersion = event.newerVersion.value
                )
            )
        )
        projection.latestSequenceNumber = sequenceNumber
        projection.update()
    }

    private fun on(event: PiiDataSubmitted, sequenceNumber: Long) {
        val projection = jooqContext.selectFrom(MERCHANT_PROJECTION)
            .where(
                MERCHANT_PROJECTION.MERCHANT_ID.eq(event.aggregateId.value),
                MERCHANT_PROJECTION.LATEST_SEQUENCE_NUMBER.eq(sequenceNumber - 1)
            )
            .single()
        val projectionData = objectMapper.readValue(projection.data!!.data(), Projection::class.java)
        projection.data = JSONB.jsonb(
            objectMapper.writeValueAsString(
                when {
                    event.version.isInitialVersion() -> projectionData.copy(
                        name = event.name.value,
                        vatNumber = event.legalEntityId.vatNumber,
                        registrationNumber = event.legalEntityId.registrationNumber,
                        countryCode = event.legalAddress.country.code,
                        postCode = event.legalAddress.postCode,
                        city = event.legalAddress.city,
                        addressLine1 = event.legalAddress.addressLine1,
                        addressLine2 = event.legalAddress.addressLine2,
                        latestSubmittedPiiDataVersion = event.version.value,
                        latestReconciledPiiDataVersion = event.version.value,
                    )

                    else -> projectionData.copy(
                        latestSubmittedPiiDataVersion = event.version.value
                    )
                }
            )
        )
        projection.latestSequenceNumber = sequenceNumber
        projection.update()
    }

    data class Projection(
        val id: UUID,
        val name: String?,
        val platformId: UUID,
        val externalId: String,
        val countryCode: String,
        val postCode: String?,
        val city: String?,
        val addressLine1: String?,
        val addressLine2: String?,
        val currencyCode: String?,
        val kitchenTypes: Set<String>,
        val vatNumber: String?,
        val registrationNumber: String?,
        val latestSubmittedPiiDataVersion: Int,
        val latestReconciledPiiDataVersion: Int,
    )
}

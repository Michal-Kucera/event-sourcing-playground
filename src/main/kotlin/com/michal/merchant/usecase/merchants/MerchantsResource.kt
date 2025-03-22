package com.michal.merchant.usecase.merchants

import com.fasterxml.jackson.databind.ObjectMapper
import com.michal.jooq.public.tables.references.MERCHANT_PROJECTION
import com.michal.merchant.domain.event.MerchantEvent
import com.michal.merchant.domain.valueobject.PiiData.Version
import com.michal.merchant.usecase.merchants.MerchantReadModelProjector.Projection
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.jooq.DSLContext
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class MerchantsResource(
    private val jooqContext: DSLContext,
    private val objectMapper: ObjectMapper,
    private val eventStorageEngine: EventStorageEngine
) {

    @GetMapping("/merchants")
    @ResponseStatus(OK)
    fun findAllMerchants() = jooqContext.selectFrom(MERCHANT_PROJECTION)
        .toList()
        .map { objectMapper.readValue(it.data!!.data(), Projection::class.java) }

    @GetMapping("/merchants/{merchant-id}")
    fun findMerchantById(@PathVariable("merchant-id") merchantId: UUID) = jooqContext.selectFrom(MERCHANT_PROJECTION)
        .where(MERCHANT_PROJECTION.MERCHANT_ID.eq(merchantId))
        .singleOrNull()
        ?.let {
            val projection = objectMapper.readValue(it.data!!.data(), Projection::class.java)
            ok().headers { headers ->
                headers.set("X-LATEST-SEQUENCE-NUMBER", it.latestSequenceNumber.toString())
            }.body(projection)
        }
        ?: notFound().build()

    @GetMapping("/merchants/{merchant-id}/pii-data/{version}")
    fun findSpecificPiiDataVersionForMerchant(
        @PathVariable("merchant-id") merchantId: UUID,
        @PathVariable("version") version: Int
    ) = eventStorageEngine.readEvents(merchantId.toString())
        .asSequence()
        .firstOrNull { (it.payload as? MerchantEvent.PiiDataSubmitted)?.version == Version.of(version) }
        ?.let { it.payload as MerchantEvent.PiiDataSubmitted }
        ?.let {
            PiiDataSubmission(
                merchantId = merchantId,
                version = version,
                name = it.name.value,
                vatNumber = it.legalEntityId.vatNumber,
                registrationNumber = it.legalEntityId.registrationNumber,
                countryCode = it.legalAddress.country.code,
                postCode = it.legalAddress.postCode,
                city = it.legalAddress.city,
                addressLine1 = it.legalAddress.addressLine1,
                addressLine2 = it.legalAddress.addressLine2,
            )
        }
        ?.let { ok(it) }
        ?: notFound().build()

    data class PiiDataSubmission(
        val merchantId: UUID,
        val version: Int,
        val name: String,
        val vatNumber: String?,
        val registrationNumber: String?,
        val countryCode: String,
        val postCode: String,
        val city: String,
        val addressLine1: String,
        val addressLine2: String?,
    )
}

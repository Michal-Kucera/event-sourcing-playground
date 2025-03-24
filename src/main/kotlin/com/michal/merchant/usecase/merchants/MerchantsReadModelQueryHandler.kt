package com.michal.merchant.usecase.merchants

import com.fasterxml.jackson.databind.ObjectMapper
import com.michal.jooq.public.tables.references.MERCHANT_PROJECTION
import com.michal.merchant.usecase.merchants.MerchantReadModelProjector.Projection
import org.axonframework.queryhandling.QueryHandler
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MerchantsReadModelQueryHandler(
    private val jooqContext: DSLContext,
    private val objectMapper: ObjectMapper,
) {

    @QueryHandler
    fun handle(query: MerchantsReadModelQuery): List<MerchantsReadModel> = jooqContext.selectFrom(MERCHANT_PROJECTION)
        .toList()
        .map { objectMapper.readValue(it.data!!.data(), Projection::class.java) }
        .map { MerchantsReadModel.from(it) }
}

object MerchantsReadModelQuery

data class MerchantsReadModel(
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
) {
    companion object {
        fun from(projection: Projection) = MerchantsReadModel(
            id = projection.id,
            name = projection.name,
            platformId = projection.platformId,
            externalId = projection.externalId,
            countryCode = projection.countryCode,
            postCode = projection.postCode,
            city = projection.city,
            addressLine1 = projection.addressLine1,
            addressLine2 = projection.addressLine2,
            currencyCode = projection.currencyCode,
            kitchenTypes = projection.kitchenTypes,
            vatNumber = projection.vatNumber,
            registrationNumber = projection.registrationNumber,
            latestSubmittedPiiDataVersion = projection.latestSubmittedPiiDataVersion,
            latestReconciledPiiDataVersion = projection.latestReconciledPiiDataVersion,
        )
    }
}

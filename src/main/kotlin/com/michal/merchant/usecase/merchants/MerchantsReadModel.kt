package com.michal.merchant.usecase.merchants

import com.michal.merchant.usecase.merchants.internal.MerchantReadModelProjector.Projection
import com.michal.sharedkernel.eventsourcing.Query
import java.util.UUID

class MerchantsReadModelQuery : Query

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
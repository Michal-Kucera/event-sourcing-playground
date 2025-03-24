package com.michal.merchant.merchants

import com.fasterxml.jackson.annotation.JsonIgnore
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.merchants.internal.MerchantReadModelProjector.Projection
import com.michal.sharedkernel.eventsourcing.Query
import java.util.UUID

data class MerchantByIdReadModelQuery(
    val merchantId: MerchantId
) : Query

data class MerchantByIdReadModel(
    val id: UUID,
    @field:JsonIgnore
    val latestSequenceNumber: Long,
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
        fun from(
            projection: Projection,
            latestSequenceNumber: Long
        ) = MerchantByIdReadModel(
            id = projection.id,
            latestSequenceNumber = latestSequenceNumber,
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

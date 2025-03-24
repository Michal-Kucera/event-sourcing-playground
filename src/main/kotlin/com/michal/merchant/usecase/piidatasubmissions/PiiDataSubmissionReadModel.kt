package com.michal.merchant.usecase.piidatasubmissions

import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.valueobject.PiiData
import com.michal.sharedkernel.eventsourcing.Query
import java.util.UUID

data class PiiDataSubmissionReadModelQuery(
    val merchantId: MerchantId,
    val version: PiiData.Version
) : Query

data class PiiDataSubmissionReadModel(
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
) {

    companion object {
        fun apply(event: PiiDataSubmitted) = PiiDataSubmissionReadModel(
            merchantId = event.aggregateId.value,
            version = event.version.value,
            name = event.name.value,
            vatNumber = event.legalEntityId.vatNumber,
            registrationNumber = event.legalEntityId.registrationNumber,
            countryCode = event.legalAddress.country.code,
            postCode = event.legalAddress.postCode,
            city = event.legalAddress.city,
            addressLine1 = event.legalAddress.addressLine1,
            addressLine2 = event.legalAddress.addressLine2,
        )
    }
}

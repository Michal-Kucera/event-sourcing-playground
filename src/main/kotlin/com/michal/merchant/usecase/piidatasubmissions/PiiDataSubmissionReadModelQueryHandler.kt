package com.michal.merchant.usecase.piidatasubmissions

import com.michal.merchant.domain.event.MerchantEvent
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.valueobject.PiiData
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class PiiDataSubmissionReadModelQueryHandler(
    private val eventStore: EventStore
) {

    @QueryHandler
    fun handleQuery(query: PiiDataSubmissionReadModelQuery): Optional<PiiDataSubmissionReadModel> = eventStore
        .readEvents(query.merchantId.toString())
        .asSequence()
        .map { it.payload }
        .filterIsInstance<MerchantEvent.PiiDataSubmitted>()
        .firstOrNull { it.version == query.version }
        ?.let { PiiDataSubmissionReadModel.apply(it) }
        .let { Optional.ofNullable(it) }
}

data class PiiDataSubmissionReadModelQuery(
    val merchantId: MerchantId,
    val version: PiiData.Version
)

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
        fun apply(event: MerchantEvent.PiiDataSubmitted) = PiiDataSubmissionReadModel(
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

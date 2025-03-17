package com.michal.domain.merchant

import com.michal.domain.sharedkernel.eventsourcing.Event

sealed interface MerchantEvent : Event<Id> {

    data class MerchantOnboarded(
        override val aggregateId: Id,
        val country: Country,
        val currency: Currency,
    ) : MerchantEvent

    data class PiiDataSubmitted(
        override val aggregateId: Id,
        val name: PiiData.Name,
        val legalEntityIdentifiers: PiiData.LegalEntityIdentifiers,
        val legalAddress: PiiData.LegalAddress
    ) : MerchantEvent
}

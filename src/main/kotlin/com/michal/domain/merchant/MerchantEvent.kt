package com.michal.domain.merchant

import com.michal.domain.merchant.AnonymizedData.KitchenType
import com.michal.domain.sharedkernel.eventsourcing.Event

sealed interface MerchantEvent : Event<Id> {

    data class MerchantOnboarded(
        override val aggregateId: Id,
        val platformId: PlatformId,
        val legalAddress: AnonymizedData.LegalAddress,
        val currency: Currency,
        val kitchenTypes: Set<KitchenType>
    ) : MerchantEvent

    data class PiiDataSubmitted(
        override val aggregateId: Id,
        val name: PiiData.Name,
        val legalEntityId: PiiData.LegalEntityId,
        val legalAddress: PiiData.LegalAddress
    ) : MerchantEvent
}

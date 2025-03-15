package com.michal.domain.merchant

import com.michal.domain.merchant.Merchant.Country
import com.michal.domain.merchant.Merchant.Currency
import com.michal.domain.merchant.Merchant.Id
import com.michal.domain.merchant.Merchant.PiiData
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
    ) : MerchantEvent
}

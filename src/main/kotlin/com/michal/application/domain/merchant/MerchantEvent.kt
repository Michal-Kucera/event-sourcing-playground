package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Country
import com.michal.application.domain.merchant.Merchant.Currency
import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.sharedkernel.eventsourcing.Event

sealed interface MerchantEvent : Event<Id> {

    data class MerchantOnboarded(
        override val aggregateId: Id,
        val country: Country,
        val currency: Currency,
    ) : MerchantEvent

    data class MerchantNameChanged(
        override val aggregateId: Id,
        val newName: Name,
    ) : MerchantEvent
}

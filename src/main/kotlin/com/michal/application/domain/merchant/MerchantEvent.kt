package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.sharedkernel.eventsourcing.Event

sealed interface MerchantEvent : Event<Id> {

    data class MerchantOnboarded(
        override val aggregateId: Id,
        val name: Name
    ) : MerchantEvent

    data class MerchantNameChanged(
        override val aggregateId: Id,
        val newName: Name,
    ) : MerchantEvent
}

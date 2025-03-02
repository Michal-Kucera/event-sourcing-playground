package com.michal.application.domain.merchant.event

import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.sharedkernel.eventsourcing.Event

data class MerchantNameChangedEvent(
    val newName: Name
) : Event

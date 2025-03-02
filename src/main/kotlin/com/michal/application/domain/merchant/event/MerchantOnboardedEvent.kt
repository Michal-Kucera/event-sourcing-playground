package com.michal.application.domain.merchant.event

import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.sharedkernel.eventsourcing.Event

data class MerchantOnboardedEvent(
    val id: Id,
    val name: Name
) : Event

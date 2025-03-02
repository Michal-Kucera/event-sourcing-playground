package com.michal.application.domain.merchant.event

import com.michal.application.domain.merchant.Merchant.Name

data class MerchantNameChangedEvent(
    val newName: Name
) : MerchantEvent

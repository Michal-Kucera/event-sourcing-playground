package com.michal.application.domain.merchant.command

import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.sharedkernel.eventsourcing.Command

data class ChangeMerchantNameCommand(
    val newName: Name
) : Command

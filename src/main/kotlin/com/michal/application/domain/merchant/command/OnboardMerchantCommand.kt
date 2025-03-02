package com.michal.application.domain.merchant.command

import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.sharedkernel.eventsourcing.Command

data class OnboardMerchantCommand(
    val id: Id,
    val name: Name
) : Command

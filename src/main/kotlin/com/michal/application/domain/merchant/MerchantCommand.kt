package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.sharedkernel.eventsourcing.Command

sealed interface MerchantCommand : Command {

    data class OnboardMerchant(
        val id: Id,
        val name: Name
    ) : MerchantCommand


    data class ChangeMerchantName(
        val newName: Name,
    ) : MerchantCommand
}

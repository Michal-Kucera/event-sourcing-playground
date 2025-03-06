package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Country
import com.michal.application.domain.merchant.Merchant.Currency
import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.sharedkernel.eventsourcing.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier

sealed interface MerchantCommand : Command<Id> {

    data class OnboardMerchant(
        @TargetAggregateIdentifier override val aggregateId: Id,
        val country: Country,
        val currency: Currency,
    ) : MerchantCommand

    data class ChangeMerchantName(
        @TargetAggregateIdentifier override val aggregateId: Id,
        val newName: Name,
    ) : MerchantCommand
}

package com.michal.domain.merchant

import com.michal.domain.sharedkernel.eventsourcing.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier

sealed interface MerchantCommand : Command<Id> {

    data class OnboardMerchant(
        @TargetAggregateIdentifier override val aggregateId: Id,
        val country: Country,
        val currency: Currency,
    ) : MerchantCommand

    data class SubmitPiiData(
        @TargetAggregateIdentifier override val aggregateId: Id,
        val name: PiiData.Name,
        val legalEntityIdentifiers: PiiData.LegalEntityIdentifiers,
    ) : MerchantCommand
}

package com.michal.merchant.domain.command

import com.michal.merchant.domain.valueobject.AnonymizedData
import com.michal.merchant.domain.valueobject.AnonymizedData.KitchenType
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.valueobject.PiiData
import com.michal.sharedkernel.eventsourcing.Command
import com.michal.sharedkernel.valueobject.Currency
import com.michal.sharedkernel.valueobject.PlatformId
import org.axonframework.modelling.command.TargetAggregateIdentifier

sealed interface MerchantCommand : Command<MerchantId> {

    data class OnboardMerchant(
        @TargetAggregateIdentifier override val aggregateId: MerchantId,
        val platformId: PlatformId,
        val legalAddress: AnonymizedData.LegalAddress,
        val currency: Currency,
        val kitchenTypes: Set<KitchenType>
    ) : MerchantCommand {
        companion object
    }

    data class SubmitPiiData(
        @TargetAggregateIdentifier override val aggregateId: MerchantId,
        val name: PiiData.Name,
        val email: PiiData.Email,
        val legalEntityId: PiiData.LegalEntityId,
        val legalAddress: PiiData.LegalAddress
    ) : MerchantCommand {
        companion object
    }

    data class ReconcilePiiData(
        @TargetAggregateIdentifier override val aggregateId: MerchantId,
        val olderVersion: PiiData.Version,
        val newerVersion: PiiData.Version,
        val reconciledName: PiiData.Name,
        val reconciledLegalEntityId: PiiData.LegalEntityId,
        val reconciledLegalAddress: PiiData.LegalAddress
    ) : MerchantCommand {
        init {
            require(olderVersion.canBeReconciledWith(newerVersion)) {
                "Cannot reconcile $olderVersion version with $newerVersion because they are not consequent versions"
            }
        }

        companion object
    }
}

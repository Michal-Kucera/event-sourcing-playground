package com.michal.merchant.domain.event

import com.michal.merchant.domain.valueobject.AnonymizedData
import com.michal.merchant.domain.valueobject.AnonymizedData.KitchenType
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.valueobject.PiiData
import com.michal.sharedkernel.eventsourcing.Event
import com.michal.sharedkernel.valueobject.Currency
import com.michal.sharedkernel.valueobject.PlatformId

sealed interface MerchantEvent : Event<MerchantId> {

    data class MerchantOnboarded(
        override val aggregateId: MerchantId,
        val platformId: PlatformId,
        val legalAddress: AnonymizedData.LegalAddress,
        val currency: Currency,
        val kitchenTypes: Set<KitchenType>
    ) : MerchantEvent {
        companion object
    }

    data class PiiDataSubmitted(
        override val aggregateId: MerchantId,
        val version: PiiData.Version,
        val name: PiiData.Name,
        val legalEntityId: PiiData.LegalEntityId,
        val legalAddress: PiiData.LegalAddress
    ) : MerchantEvent {
        companion object
    }

    data class PiiDataReconciled(
        override val aggregateId: MerchantId,
        val olderVersion: PiiData.Version,
        val newerVersion: PiiData.Version,
        val reconciledName: PiiData.Name,
        val reconciledLegalEntityId: PiiData.LegalEntityId,
        val reconciledLegalAddress: PiiData.LegalAddress
    ) : MerchantEvent {
        companion object
    }
}

package com.michal.application.usecase.merchant

import com.michal.application.domain.merchant.Merchant
import com.michal.application.domain.merchant.MerchantCommand.ChangeMerchantName
import com.michal.application.domain.merchant.MerchantEventStore

class ChangeMerchantNameUseCase(
    private val eventStore: MerchantEventStore
) {
    operator fun invoke(command: Command) {
        val merchant = eventStore.getBy(command.merchantId)
            .handle(ChangeMerchantName(command.newMerchantName))
        eventStore.storeEventsFor(merchant)
    }

    data class Command(
        val merchantId: Merchant.Id,
        val newMerchantName: Merchant.Name
    )
}

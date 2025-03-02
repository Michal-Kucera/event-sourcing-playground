package com.michal.application.usecase.merchant

import com.michal.application.domain.merchant.Merchant
import com.michal.application.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.application.domain.merchant.MerchantEventStore

class OnboardMerchantUseCase(
    private val eventStore: MerchantEventStore,
) {
    operator fun invoke(command: Command) {
        val merchant = Merchant.handle(OnboardMerchant(command.merchantId, command.merchantName))
        eventStore.storeEventsFor(merchant)
    }

    data class Command(
        val merchantId: Merchant.Id,
        val merchantName: Merchant.Name
    )
}

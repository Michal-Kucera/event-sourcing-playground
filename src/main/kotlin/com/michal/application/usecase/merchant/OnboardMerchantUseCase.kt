package com.michal.application.usecase.merchant

import com.michal.application.domain.merchant.Merchant
import com.michal.application.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.application.domain.merchant.MerchantEventStore
import com.michal.application.usecase.merchant.OnboardMerchantUseCase.Method.COMMAND
import com.michal.application.usecase.merchant.OnboardMerchantUseCase.Method.METHOD_CALL

class OnboardMerchantUseCase(
    private val eventStore: MerchantEventStore,
    private val useMethod: Method
) {
    operator fun invoke(command: Command) {
        when (useMethod) {
            COMMAND -> onboardMerchantViaCommandHandler(command)
            METHOD_CALL -> onboardMerchantViaMethodCall(command)
        }
    }

    private fun onboardMerchantViaMethodCall(command: Command) {
        val merchant = Merchant.onboard(command.merchantId, command.merchantName)
        eventStore.storeEventsFor(merchant)
    }

    private fun onboardMerchantViaCommandHandler(command: Command) {
        val merchant = Merchant.handle(OnboardMerchant(command.merchantId, command.merchantName))
        eventStore.storeEventsFor(merchant)
    }

    data class Command(
        val merchantId: Merchant.Id,
        val merchantName: Merchant.Name
    )

    enum class Method {
        COMMAND,
        METHOD_CALL
    }
}

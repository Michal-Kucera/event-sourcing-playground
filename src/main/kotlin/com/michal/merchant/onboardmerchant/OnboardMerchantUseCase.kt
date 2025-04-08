package com.michal.merchant.onboardmerchant

import com.michal.merchant.domain.Merchant
import com.michal.merchant.domain.command.MerchantCommand.OnboardMerchant
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.Repository

//@Component
class OnboardMerchantUseCase(
    private val repository: Repository<Merchant>
) {

    @CommandHandler
    fun handle(command: OnboardMerchant) {
        repository.newInstance(::Merchant).execute {
            applyEvent(
                MerchantOnboarded(
                    command.aggregateId,
                    command.platformId,
                    command.legalAddress,
                    command.currency,
                    command.kitchenTypes
                )
            )
        }
    }
}

@file:Suppress("unused")

package com.michal.config

import com.michal.adapter.merchant.InMemoryMerchantEventStore
import com.michal.application.domain.merchant.MerchantEventStore
import com.michal.application.usecase.merchant.ChangeMerchantNameUseCase
import com.michal.application.usecase.merchant.OnboardMerchantUseCase
import com.michal.application.usecase.merchant.OnboardMerchantUseCase.Method
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    inner class MerchantConfiguration {

        @Bean
        fun merchantEventStore(): MerchantEventStore = InMemoryMerchantEventStore()

        @Bean
        fun onboardMerchantUseCase() = OnboardMerchantUseCase(merchantEventStore(), Method.COMMAND)

        @Bean
        fun changeMerchantNameUseCase() = ChangeMerchantNameUseCase(merchantEventStore())
    }
}

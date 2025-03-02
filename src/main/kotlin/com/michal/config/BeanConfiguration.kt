@file:Suppress("unused")

package com.michal.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.michal.adapter.merchant.PostgresMerchantEventStore
import com.michal.application.domain.merchant.MerchantEventStore
import com.michal.application.usecase.merchant.ChangeMerchantNameUseCase
import com.michal.application.usecase.merchant.OnboardMerchantUseCase
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    inner class MerchantConfiguration(
        private val dslContext: DSLContext,
        private val objectMapper: ObjectMapper
    ) {

        @Bean
        fun merchantEventStore(): MerchantEventStore = PostgresMerchantEventStore(dslContext, objectMapper)

        @Bean
        fun onboardMerchantUseCase() = OnboardMerchantUseCase(merchantEventStore())

        @Bean
        fun changeMerchantNameUseCase() = ChangeMerchantNameUseCase(merchantEventStore())
    }
}

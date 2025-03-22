package com.michal.merchant.usecase.merchants

import com.michal.merchant.domain.event.MerchantEvent
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataReconciled
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
@ProcessingGroup("merchants")
class MerchantReadModelProjector {

    @EventHandler
    fun on(event: MerchantEvent) {
        println("Applying ${event.javaClass.simpleName} to merchant ${event.aggregateId} projection")
        when (event) {
            is MerchantOnboarded -> {
                // TODO: do something
            }

            is PiiDataReconciled -> {
                // TODO: do something
            }

            is PiiDataSubmitted -> {
                // TODO: do something
            }
        }
        if (Random.nextBoolean()) {
            error("Merchant ${event.aggregateId} is not good!")
        }
    }
}

package com.michal.merchant.usecase.merchants

import com.michal.merchant.domain.event.MerchantEvent
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
        if (Random.nextBoolean()) {
            error("Merchant ${event.aggregateId} is not good!")
        }
    }
}

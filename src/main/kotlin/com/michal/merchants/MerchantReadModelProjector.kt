package com.michal.merchants

import com.michal.application.domain.merchant.MerchantEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class MerchantReadModelProjector {

    @EventHandler
    fun invoke(event: MerchantEvent) {
        println("Applying ${event.javaClass.simpleName} to merchant projection")
    }
}

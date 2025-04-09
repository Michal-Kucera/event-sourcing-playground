package com.michal.merchant.sendwelcomeemail

import com.michal.merchant.domain.command.MerchantCommand.SendWelcomeEmail
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.DisallowReplay
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@DisallowReplay
@Component
@ProcessingGroup("merchants")
class SendWelcomeEmailAutomationProcessor(
    private val commandGateway: CommandGateway,
) {

    @EventHandler
    fun on(event: PiiDataSubmitted) {
        if (event.version.isInitialVersion()) {
            commandGateway.sendAndWait<Any>(
                SendWelcomeEmail(event.aggregateId, "Welcome onboard, {{name}}!"),
            )
        }
    }
}

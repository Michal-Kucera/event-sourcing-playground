package com.michal.merchant.sendwelcomeemail

import com.michal.merchant.domain.Merchant
import com.michal.merchant.domain.command.MerchantCommand.SendWelcomeEmail
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.modelling.command.Repository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional

@Component
class SendWelcomeEmailCommandHandler(
    private val repository: Repository<Merchant>,
    private val emailSender: EmailSender,
    @Value("\${axon.eventhandling.processors.merchants.simulate-failure}") private val shouldFail: Boolean
) {

    @CommandHandler
    @Transactional(propagation = REQUIRES_NEW)
    fun handle(command: SendWelcomeEmail) {
        repository.load(command.aggregateId.toString())?.execute { merchant ->
            val currentPiiData = merchant.piiData.currentPiiData()
            val to = currentPiiData.email
            val content = command.contentTemplate.replace("{{name}}", currentPiiData.name.value)
            merchant.sendWelcomeEmail(to, content)

            emailSender.sendEmail(to, content)

            if (shouldFail) {
                error(
                    "️🙃 Oh no! Something has gone wrong with merchant ${command.aggregateId}" +
                            " while sending welcome email!"
                )
            }
        }
    }
}

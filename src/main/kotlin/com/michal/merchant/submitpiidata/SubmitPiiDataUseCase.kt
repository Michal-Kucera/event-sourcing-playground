package com.michal.merchant.submitpiidata

import com.michal.merchant.domain.Merchant
import com.michal.merchant.domain.command.MerchantCommand.SubmitPiiData
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.Repository

//@Component
class SubmitPiiDataUseCase(
    private val repository: Repository<Merchant>
) {

    @CommandHandler
    fun handle(command: SubmitPiiData) {
        repository.load(command.aggregateId.toString()).execute { merchant ->
            require(merchant.anonymizedData.hasSame(command.legalAddress.country)) {
                "Submitted PII data has different country (${command.legalAddress.country}) " +
                        "than anonymized data (${merchant.anonymizedData.legalAddress.country})"
            }
            applyEvent(
                PiiDataSubmitted(
                    command.aggregateId,
                    merchant.piiData.nextVersion(),
                    command.name,
                    command.email,
                    command.legalEntityId,
                    command.legalAddress
                )
            )
        }
    }
}

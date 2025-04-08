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
        with(command) {
            repository.load(aggregateId.toString()).execute {
                require(it.anonymizedData.hasSame(legalAddress.country)) {
                    "Submitted PII data has different country (${legalAddress.country}) " +
                            "than anonymized data (${it.anonymizedData.legalAddress.country})"
                }
                applyEvent(PiiDataSubmitted(aggregateId, it.piiData.nextVersion(), name, legalEntityId, legalAddress))
            }
        }
    }
}

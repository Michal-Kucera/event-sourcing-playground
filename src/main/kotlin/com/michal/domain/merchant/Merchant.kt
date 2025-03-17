package com.michal.domain.merchant

import com.michal.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.domain.merchant.MerchantCommand.SubmitPiiData
import com.michal.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.domain.merchant.MerchantEvent.PiiDataSubmitted
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateCreationPolicy.ALWAYS
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateRoot
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
@AggregateRoot
class Merchant {

    @AggregateIdentifier
    private lateinit var aggregateId: Id
    private lateinit var anonymizedData: AnonymizedData
    private var piiData: PiiData? = null

    @CommandHandler
    @CreationPolicy(ALWAYS)
    fun handle(command: OnboardMerchant) {
        applyEvent(MerchantOnboarded(command.aggregateId, command.country, command.currency))
    }

    @CommandHandler
    fun handle(command: SubmitPiiData) {
        require(piiData == null) { "PII data cannot be submitted multiple times" }
        require(command.legalAddress.country == anonymizedData.country) {
            "Submitted PII data has different country (${command.legalAddress.country}) " +
                    "than anonymized data (${anonymizedData.country})"
        }
        applyEvent(PiiDataSubmitted(aggregateId, command.name, command.legalEntityIdentifiers, command.legalAddress))
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(event: MerchantOnboarded) {
        aggregateId = event.aggregateId
        anonymizedData = AnonymizedData.create(event.country, event.currency)
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(event: PiiDataSubmitted) {
        piiData = PiiData.with(event.name, event.legalEntityIdentifiers, event.legalAddress)
    }
}

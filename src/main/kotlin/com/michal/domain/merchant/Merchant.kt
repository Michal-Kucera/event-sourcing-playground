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
    private lateinit var country: Country
    private lateinit var currency: Currency
    private var piiData: PiiData? = null

    @CommandHandler
    @CreationPolicy(ALWAYS)
    fun handle(command: OnboardMerchant) {
        applyEvent(MerchantOnboarded(command.aggregateId, command.country, command.currency))
    }

    @CommandHandler
    fun handle(command: SubmitPiiData) {
        require(piiData == null) { "PII data cannot be submitted multiple times" }
        applyEvent(PiiDataSubmitted(aggregateId, command.name, command.legalEntityIdentifiers, command.address))
    }

    // Option #2 for CommandHandler
    //    @CommandHandler
    //    @CreationPolicy(CREATE_IF_MISSING)
    fun handle(command: MerchantCommand) = when (command) {
        is OnboardMerchant -> {
            require(!::aggregateId.isInitialized) { "Merchant ${command.aggregateId} is already onboarded" }
            applyEvent(MerchantOnboarded(command.aggregateId, command.country, command.currency))
        }

        is SubmitPiiData -> {
            require(piiData == null) { "PII data cannot be submitted multiple times" }
            applyEvent(PiiDataSubmitted(aggregateId, command.name, command.legalEntityIdentifiers, command.address))
        }
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(event: MerchantOnboarded) {
        aggregateId = event.aggregateId
        country = event.country
        currency = event.currency
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(event: PiiDataSubmitted) {
        piiData = PiiData.with(event.name, event.legalEntityIdentifiers, event.address)
    }

    // Option #2 for EventSourcingHandler
    //    @EventSourcingHandler
    //    @Suppress("unused")
    fun on(event: MerchantEvent) = when (event) {
        is MerchantOnboarded -> {
            aggregateId = event.aggregateId
            country = event.country
            currency = event.currency
        }

        is PiiDataSubmitted -> piiData = PiiData.with(event.name, event.legalEntityIdentifiers, event.address)
    }
}

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
    private lateinit var platformId: PlatformId
    private lateinit var currency: Currency
    private lateinit var anonymizedData: AnonymizedData
    private var piiData: PiiDataCollection = PiiDataCollection.withNoPiiData()

    @CommandHandler
    @CreationPolicy(ALWAYS)
    fun handle(command: OnboardMerchant) {
        with(command) {
            applyEvent(MerchantOnboarded(aggregateId, platformId, legalAddress, currency, kitchenTypes))
        }
    }

    @CommandHandler
    fun handle(command: SubmitPiiData) {
        require(piiData.hasNoPiiData()) { "PII data cannot be submitted multiple times" }
        require(command.legalAddress.country == anonymizedData.legalAddress.country) {
            "Submitted PII data has different country (${command.legalAddress.country}) " +
                    "than anonymized data (${anonymizedData.legalAddress.country})"
        }
        applyEvent(PiiDataSubmitted(aggregateId, command.name, command.legalEntityId, command.legalAddress))
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(event: MerchantOnboarded) {
        aggregateId = event.aggregateId
        platformId = event.platformId
        currency = event.currency
        anonymizedData = AnonymizedData.with(event.legalAddress, event.kitchenTypes)
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(event: PiiDataSubmitted) {
        piiData = piiData.add(PiiData.with(event.name, event.legalEntityId, event.legalAddress))
    }
}

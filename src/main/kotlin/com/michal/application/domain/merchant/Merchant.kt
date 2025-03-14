package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.MerchantCommand.ChangeMerchantName
import com.michal.application.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateCreationPolicy.ALWAYS
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateRoot
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

@Aggregate
@AggregateRoot
class Merchant {

    @AggregateIdentifier
    private lateinit var aggregateId: Id
    private lateinit var country: Country
    private lateinit var currency: Currency
    private var name: Name? = null

    @CommandHandler
    @CreationPolicy(ALWAYS)
    fun handle(command: OnboardMerchant) {
        applyEvent(MerchantOnboarded(command.aggregateId, command.country, command.currency))
    }

    @CommandHandler
    fun handle(command: ChangeMerchantName) {
        applyEvent(MerchantNameChanged(aggregateId, command.newName))
    }

    // Option #2 for CommandHandler
//    @CommandHandler
//    @CreationPolicy(CREATE_IF_MISSING)
//    fun handle(command: MerchantCommand) = when (command) {
//        is OnboardMerchant -> {
//            require(!::aggregateId.isInitialized) { "Merchant ${command.aggregateId} is already onboarded" }
//            applyEvent(MerchantOnboarded(command.aggregateId, command.country, command.currency))
//        }
//
//        is ChangeMerchantName -> applyEvent(MerchantNameChanged(aggregateId, command.newName))
//    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(event: MerchantOnboarded) {
        aggregateId = event.aggregateId
        country = event.country
        currency = event.currency
    }

    @EventSourcingHandler
    @Suppress("unused")
    fun on(event: MerchantNameChanged) {
        name = event.newName
    }

    // Option #2 for EventSourcingHandler
//    @EventSourcingHandler
//    @Suppress("unused")
//    fun on(event: MerchantEvent) = when (event) {
//        is MerchantOnboarded -> {
//            aggregateId = event.aggregateId
//            country = event.country
//            currency = event.currency
//        }
//
//        is MerchantNameChanged -> {
//            name = event.newName
//        }
//    }

    data class Id private constructor(
        val value: UUID
    ) {
        override fun toString(): String = value.toString()

        companion object {
            fun of(id: UUID): Id = Id(id)
        }
    }

    data class Country private constructor(val code: String) {
        companion object {
            fun from(countryCode: String): Country {
                require(countryCode.trim().length == 3) { "Country code must be in ISO-3 format" }
                return Country(countryCode)
            }

            val GERMANY: Country = from("DEU")
            val UNITED_STATES_OF_AMERICA: Country = from("USA")
        }
    }

    data class Currency private constructor(val code: java.util.Currency) {
        companion object {
            fun from(currencyCode: String): Currency = Currency(java.util.Currency.getInstance(currencyCode))

            val EUR: Currency = from("EUR")
            val USD: Currency = from("USD")
        }
    }

    data class Name private constructor(
        val name: String
    ) {
        override fun toString(): String = name

        companion object {
            fun of(name: String): Name {
                require(name.isNotBlank()) { "Name cannot be blank" }
                return Name(name)
            }
        }
    }
}

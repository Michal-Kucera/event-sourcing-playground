package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.MerchantCommand.ChangeMerchantName
import com.michal.application.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy.CREATE_IF_MISSING
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate
import java.util.Currency
import java.util.UUID

@Aggregate
class Merchant {

    @AggregateIdentifier
    private lateinit var aggregateId: Id
    private lateinit var country: Country
    private lateinit var currency: Currency
    private var name: Name? = null

    @CommandHandler
    @CreationPolicy(CREATE_IF_MISSING)
    fun handle(command: OnboardMerchant) {
        AggregateLifecycle.apply(
            MerchantOnboarded(
                aggregateId = command.aggregateId,
                country = command.country,
                currency = command.currency,
            )
        )
    }

    @CommandHandler
    fun handle(command: ChangeMerchantName) {
        AggregateLifecycle.apply(MerchantNameChanged(aggregateId, command.newName))
    }

    @EventSourcingHandler
    fun on(event: MerchantOnboarded) {
        aggregateId = event.aggregateId
        country = event.country
        currency = event.currency
    }

    @EventSourcingHandler
    fun on(event: MerchantNameChanged) {
        name = event.newName
    }

    data class Id private constructor(
        val id: UUID
    ) {
        override fun toString(): String = id.toString()

        companion object {
            fun of(id: UUID): Id = Id(id)
        }
    }

    data class Country private constructor(val code: String) {
        companion object {
            private fun from(countryCode: String): Country {
                require(countryCode.trim().length == 3) { "Country code must be in ISO-3 format" }
                return Country(countryCode)
            }

            val GERMANY: Country = from("DEU")
            val UNITED_STATES_OF_AMERICA: Country = from("USA")
        }
    }

    data class Currency private constructor(val currency: java.util.Currency) {
        companion object {
            private fun from(currencyCode: String): Currency = Currency(java.util.Currency.getInstance(currencyCode))

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

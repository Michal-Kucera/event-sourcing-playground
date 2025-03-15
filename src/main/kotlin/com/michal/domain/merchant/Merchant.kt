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
import java.util.UUID

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
        applyEvent(PiiDataSubmitted(aggregateId, command.name, command.legalEntityIdentifiers))
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
//        is SubmitPiiData -> {
//            require(piiData == null) { "PII data cannot be submitted multiple times" }
//            applyEvent(PiiDataSubmitted(aggregateId, command.name, command.legalEntityIdentifiers))
//        }
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
    fun on(event: PiiDataSubmitted) {
        piiData = PiiData.with(event.name, event.legalEntityIdentifiers)
    }

//    // Option #2 for EventSourcingHandler
//    @EventSourcingHandler
//    @Suppress("unused")
//    fun on(event: MerchantEvent) = when (event) {
//        is MerchantOnboarded -> {
//            aggregateId = event.aggregateId
//            country = event.country
//            currency = event.currency
//        }
//
//        is PiiDataSubmitted -> piiData = PiiData.of(event.name, event.legalEntityIdentifiers)
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

    data class PiiData private constructor(
        val name: Name,
        val legalEntityIdentifiers: LegalEntityIdentifiers
    ) {
        companion object {
            fun with(
                name: Name,
                legalEntityIdentifiers: LegalEntityIdentifiers
            ): PiiData = PiiData(name, legalEntityIdentifiers)
        }

        data class Name private constructor(
            val value: String
        ) {
            override fun toString(): String = value

            companion object {
                fun of(name: String): Name {
                    require(name.isNotBlank()) { "Name cannot be blank" }
                    return Name(name)
                }
            }
        }

        data class LegalEntityIdentifiers private constructor(
            val vatNumber: String?,
            val registrationNumber: String?
        ) {

            companion object {
                fun of(
                    vatNumber: String?,
                    registrationNumber: String?
                ): LegalEntityIdentifiers {
                    require(!vatNumber.isNullOrBlank() || !registrationNumber.isNullOrBlank()) {
                        "At least one of VAT number or registration number must be provided"
                    }
                    return LegalEntityIdentifiers(vatNumber, registrationNumber)
                }
            }
        }
    }
}

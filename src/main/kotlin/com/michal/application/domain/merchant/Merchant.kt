package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.MerchantCommand.ChangeMerchantName
import com.michal.application.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.application.domain.sharedkernel.eventsourcing.AggregateVersion
import com.michal.application.domain.sharedkernel.eventsourcing.EventSourcedAggregate
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

data class Merchant private constructor(
    override val aggregateId: Id,
    override val version: AggregateVersion,
    val platformId: UUID,
    val foundingDate: LocalDate?,
    val platformPartnershipStartedDate: LocalDate?,
    val businessTypes: List<String>,
    val preferredCurrency: Currency,
    val paymentAccountNumberEnding: String,
    val legalRepresentativePhoneEnding: String,
    val countryCode: String,
    val postCode: String?,
    val city: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val name: Name?,
) : EventSourcedAggregate<Id, MerchantEvent>(aggregateId, version) {

    // command handler impl #1
    // enforce invariants, validate business rules and apply the correct events in case all rules apply
    fun handle(command: ChangeMerchantName): Merchant {
        if (name == null || !name.isSameAs(command.newName)) {
            append(MerchantNameChanged(aggregateId, command.newName))
        }
        return this
    }

    // command handler impl #2
    fun handle(command: MerchantCommand): Merchant {
        when (command) {
            is OnboardMerchant -> error("${command.javaClass.simpleName} command cannot be handled")
            is ChangeMerchantName -> if (name == null || !name.isSameAs(command.newName)) {
                append(MerchantNameChanged(aggregateId, command.newName))
            }
        }
        return this
    }

    // function call instead of command handler
    fun changeName(newName: Name): Merchant {
        if (name == null || !name.isSameAs(newName)) {
            append(MerchantNameChanged(aggregateId, newName))
        }
        return this
    }

    // event-sourcing handler impl #1
    // change the state of the aggregate
    fun on(event: MerchantNameChanged): Merchant = copy(name = event.newName)

    // Impl #2
    fun on(event: MerchantEvent): Merchant = when (event) {
        is MerchantOnboarded -> error("${event.javaClass.simpleName} event cannot be applied")
        is MerchantNameChanged -> copy(name = event.newName)
    }

    companion object {
        // Command handler
        fun handle(
            command: OnboardMerchant
        ): Merchant = Merchant(
            aggregateId = command.id,
            version = AggregateVersion.initialVersion(),
            platformId = command.platformId,
            foundingDate = command.foundingDate,
            platformPartnershipStartedDate = command.platformPartnershipStartedDate,
            businessTypes = command.businessTypes,
            preferredCurrency = command.preferredCurrency,
            paymentAccountNumberEnding = command.paymentAccountNumberEnding,
            legalRepresentativePhoneEnding = command.legalRepresentativePhoneEnding,
            countryCode = command.countryCode,
            postCode = command.postCode,
            city = command.city,
            addressLine1 = command.addressLine1,
            addressLine2 = command.addressLine2,
            name = null,
        ).apply {
            append(
                MerchantOnboarded(
                    aggregateId = command.id,
                    platformId = platformId,
                    foundingDate = foundingDate,
                    platformPartnershipStartedDate = platformPartnershipStartedDate,
                    businessTypes = businessTypes,
                    preferredCurrency = preferredCurrency,
                    paymentAccountNumberEnding = paymentAccountNumberEnding,
                    legalRepresentativePhoneEnding = legalRepresentativePhoneEnding,
                    countryCode = countryCode,
                    postCode = postCode,
                    city = city,
                    addressLine1 = addressLine1,
                    addressLine2 = addressLine2,
                )
            )
        }

        // function call instead of command handler
        fun onboard(
            id: Id,
            platformId: UUID,
            foundingDate: LocalDate?,
            platformPartnershipStartedDate: LocalDate?,
            businessTypes: List<String>,
            preferredCurrency: Currency,
            paymentAccountNumberEnding: String,
            legalRepresentativePhoneEnding: String,
            countryCode: String,
            postCode: String?,
            city: String?,
            addressLine1: String?,
            addressLine2: String?,
        ): Merchant = Merchant(
            aggregateId = id,
            version = AggregateVersion.initialVersion(),
            platformId = platformId,
            foundingDate = foundingDate,
            platformPartnershipStartedDate = platformPartnershipStartedDate,
            businessTypes = businessTypes,
            preferredCurrency = preferredCurrency,
            paymentAccountNumberEnding = paymentAccountNumberEnding,
            legalRepresentativePhoneEnding = legalRepresentativePhoneEnding,
            countryCode = countryCode,
            postCode = postCode,
            city = city,
            addressLine1 = addressLine1,
            addressLine2 = addressLine2,
            name = null,
        ).apply {
            append(
                MerchantOnboarded(
                    aggregateId = id,
                    platformId = platformId,
                    foundingDate = foundingDate,
                    platformPartnershipStartedDate = platformPartnershipStartedDate,
                    businessTypes = businessTypes,
                    preferredCurrency = preferredCurrency,
                    paymentAccountNumberEnding = paymentAccountNumberEnding,
                    legalRepresentativePhoneEnding = legalRepresentativePhoneEnding,
                    countryCode = countryCode,
                    postCode = postCode,
                    city = city,
                    addressLine1 = addressLine1,
                    addressLine2 = addressLine2,
                )
            )
        }

        fun on(
            event: MerchantOnboarded
        ): Merchant = Merchant(
            aggregateId = event.aggregateId,
            version = AggregateVersion.initialVersion(),
            platformId = event.platformId,
            foundingDate = event.foundingDate,
            platformPartnershipStartedDate = event.platformPartnershipStartedDate,
            businessTypes = event.businessTypes,
            preferredCurrency = event.preferredCurrency,
            paymentAccountNumberEnding = event.paymentAccountNumberEnding,
            legalRepresentativePhoneEnding = event.legalRepresentativePhoneEnding,
            countryCode = event.countryCode,
            postCode = event.postCode,
            city = event.city,
            addressLine1 = event.addressLine1,
            addressLine2 = event.addressLine2,
            name = null
        )
    }

    data class Id private constructor(
        val id: UUID
    ) {
        override fun toString(): String = id.toString()

        companion object {
            fun of(id: UUID): Id = Id(id)
        }
    }

    data class Name private constructor(
        val name: String
    ) {
        override fun toString(): String = name

        fun isSameAs(other: Name): Boolean = name == other.name

        companion object {
            fun of(name: String): Name {
                require(name.isNotBlank()) { "Name cannot be blank" }
                return Name(name)
            }
        }
    }
}

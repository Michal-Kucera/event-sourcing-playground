package com.michal.application.usecase.merchant

import com.michal.adapter.merchant.InMemoryMerchantEventStore
import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.merchant.MerchantEvent
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.application.domain.sharedkernel.eventsourcing.EventStore.AggregateNotFound
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

class MerchantUseCaseTest {

    private val eventStore = InMemoryMerchantEventStore()
    private val changeMerchantNameUseCase = ChangeMerchantNameUseCase(eventStore)
    private val onboardMerchantUseCase = OnboardMerchantUseCase(eventStore)

    @Nested
    inner class Onboard {

        @Test
        fun `onboards a new merchant`() {
            onboardMerchant()

            shouldContain(
                MerchantOnboarded(
                    aggregateId = merchantId(),
                    platformId = UUID.fromString("5cf85b1d-e233-4045-a24a-75d5a755e3cb"),
                    foundingDate = LocalDate.of(2020, 9, 20),
                    platformPartnershipStartedDate = LocalDate.of(2021, 1, 5),
                    businessTypes = listOf("Asian", "Korean"),
                    preferredCurrency = Currency.getInstance("EUR"),
                    paymentAccountNumberEnding = "1234",
                    legalRepresentativePhoneEnding = "6789",
                    countryCode = "DEU",
                    postCode = "08320",
                    city = "El Masnou",
                    addressLine1 = "Carrer De Blai",
                    addressLine2 = "A/B",
                )
            )
        }
    }

    @Nested
    inner class ChangeName {

        @Test
        fun `changes name of an existing merchant`() {
            onboardMerchant()

            changeNameOfMerchant()

            shouldContain(
                MerchantOnboarded(
                    aggregateId = merchantId(),
                    platformId = UUID.fromString("5cf85b1d-e233-4045-a24a-75d5a755e3cb"),
                    foundingDate = LocalDate.of(2020, 9, 20),
                    platformPartnershipStartedDate = LocalDate.of(2021, 1, 5),
                    businessTypes = listOf("Asian", "Korean"),
                    preferredCurrency = Currency.getInstance("EUR"),
                    paymentAccountNumberEnding = "1234",
                    legalRepresentativePhoneEnding = "6789",
                    countryCode = "DEU",
                    postCode = "08320",
                    city = "El Masnou",
                    addressLine1 = "Carrer De Blai",
                    addressLine2 = "A/B",
                ),
                MerchantNameChanged(merchantId(), merchantName())
            )
        }

        @Test
        fun `fails when trying to change name of a non-existing merchant`() {
            shouldThrowWithMessage<AggregateNotFound>("Merchant ${merchantId()} not found") { changeNameOfMerchant() }

            shouldContainNoEvents()
        }
    }

    private fun onboardMerchant() = onboardMerchantUseCase(
        OnboardMerchantUseCase.Command(
            merchantId = merchantId(),
            platformId = UUID.fromString("5cf85b1d-e233-4045-a24a-75d5a755e3cb"),
            foundingDate = LocalDate.of(2020, 9, 20),
            platformPartnershipStartedDate = LocalDate.of(2021, 1, 5),
            businessTypes = listOf("Asian", "Korean"),
            preferredCurrency = Currency.getInstance("EUR"),
            paymentAccountNumberEnding = "1234",
            legalRepresentativePhoneEnding = "6789",
            countryCode = "DEU",
            postCode = "08320",
            city = "El Masnou",
            addressLine1 = "Carrer De Blai",
            addressLine2 = "A/B",
        )
    )

    private fun changeNameOfMerchant() = changeMerchantNameUseCase(
        ChangeMerchantNameUseCase.Command(merchantId(), merchantName())
    )

    private fun shouldContain(vararg events: MerchantEvent) =
        eventStore.eventsFor(merchantId()) shouldBe events.toList()

    private fun shouldContainNoEvents() = eventStore.eventsFor(merchantId()).shouldBeEmpty()

    private fun merchantName() = Name.of("Arun Murmu")

    private fun merchantId() = Id.of(UUID.fromString("c9978564-b770-43c2-9017-3e2be3e4d875"))
}

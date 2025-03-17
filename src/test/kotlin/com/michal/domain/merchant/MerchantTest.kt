package com.michal.domain.merchant

import com.michal.domain.merchant.AnonymizedData.KitchenType
import com.michal.domain.merchant.Country.Companion.UNITED_STATES_OF_AMERICA
import com.michal.domain.merchant.Currency.Companion.USD
import com.michal.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.domain.merchant.MerchantCommand.SubmitPiiData
import com.michal.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.domain.merchant.MerchantEvent.PiiDataSubmitted
import org.axonframework.extension.kotlin.test.aggregateTestFixture
import org.axonframework.extension.kotlin.test.whenever
import org.junit.jupiter.api.Test
import java.util.UUID

class MerchantTest {

    private val fixture = aggregateTestFixture<Merchant>()

    @Test
    fun `onboards a new merchant`() {
        fixture
            .whenever(onboardMerchant())
            .expectSuccessfulHandlerExecution()
            .expectEvents(merchantOnboarded())
    }

    @Test
    fun `submits PII data for an existing merchant`() {
        fixture
            .given(merchantOnboarded())
            .whenever(submitPiiData())
            .expectSuccessfulHandlerExecution()
            .expectEvents(piiDataSubmitted())
    }

    private fun onboardMerchant() =
        OnboardMerchant(merchantId(), platformId(), anonymizedAddress(), USD, kitchenTypes())

    private fun merchantOnboarded() =
        MerchantOnboarded(merchantId(), platformId(), anonymizedAddress(), USD, kitchenTypes())

    private fun submitPiiData() = SubmitPiiData(merchantId(), merchantName(), legalEntityId(), address())

    private fun piiDataSubmitted() = PiiDataSubmitted(merchantId(), merchantName(), legalEntityId(), address())

    private fun merchantName() = PiiData.Name.of("Norma Gan")

    private fun legalEntityId() = PiiData.LegalEntityId.of(
        country = UNITED_STATES_OF_AMERICA,
        vatNumber = "123456789",
        registrationNumber = "987654321"
    )

    private fun platformId() = PlatformId.of(
        platformId = UUID.fromString("e6ceecdb-5ad0-454d-a428-f9f0f873f69b"),
        merchantExternalId = "026c516959354797bd1a7bdc03e2e8c4"
    )

    private fun anonymizedAddress() = AnonymizedData.LegalAddress.of(
        country = UNITED_STATES_OF_AMERICA,
        postCode = "08030",
        city = "Washington DC",
        addressLine1 = "Trumpstreet",
        addressLine2 = null
    )

    private fun kitchenTypes() = setOf(KitchenType.of("Asian"), KitchenType.of("Korean"))

    private fun address() = PiiData.LegalAddress.of(
        country = UNITED_STATES_OF_AMERICA,
        postCode = "08030",
        city = "Washington DC",
        addressLine1 = "Trumpstreet 4",
        addressLine2 = "Block 1"
    )

    private fun merchantId() = Id.of(UUID.fromString("9cbf676b-552b-460d-8da4-029e97ca95b7"))
}

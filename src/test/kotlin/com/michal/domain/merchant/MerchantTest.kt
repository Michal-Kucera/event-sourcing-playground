package com.michal.domain.merchant

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

    private fun onboardMerchant() = OnboardMerchant(merchantId(), UNITED_STATES_OF_AMERICA, USD)

    private fun merchantOnboarded() = MerchantOnboarded(merchantId(), UNITED_STATES_OF_AMERICA, USD)

    private fun submitPiiData() = SubmitPiiData(merchantId(), merchantName(), legalEntityIdentifiers(), address())

    private fun piiDataSubmitted() = PiiDataSubmitted(merchantId(), merchantName(), legalEntityIdentifiers(), address())

    private fun merchantName() = PiiData.Name.of("Norma Gan")

    private fun legalEntityIdentifiers() = PiiData.LegalEntityIdentifiers.of(
        country = UNITED_STATES_OF_AMERICA,
        vatNumber = "123456789",
        registrationNumber = "987654321"
    )

    private fun address() = PiiData.LegalAddress.of(
        country = UNITED_STATES_OF_AMERICA,
        postCode = "08030",
        city = "Washington DC",
        addressLine1 = "Trumpstreet 4",
        addressLine2 = "Block 1"
    )

    private fun merchantId() = Id.of(UUID.fromString("9cbf676b-552b-460d-8da4-029e97ca95b7"))
}

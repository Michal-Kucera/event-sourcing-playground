package com.michal.domain.merchant

import com.michal.domain.merchant.Merchant.Country.Companion.UNITED_STATES_OF_AMERICA
import com.michal.domain.merchant.Merchant.Currency.Companion.USD
import com.michal.domain.merchant.Merchant.Id
import com.michal.domain.merchant.Merchant.PiiData
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

    private fun submitPiiData() = SubmitPiiData(merchantId(), merchantName())

    private fun piiDataSubmitted() = PiiDataSubmitted(merchantId(), merchantName())

    private fun merchantName() = PiiData.Name.of("Norma Gan")

    private fun merchantId() = Id.of(UUID.fromString("9cbf676b-552b-460d-8da4-029e97ca95b7"))
}

package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Country.Companion.UNITED_STATES_OF_AMERICA
import com.michal.application.domain.merchant.Merchant.Currency.Companion.USD
import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.merchant.MerchantCommand.ChangeMerchantName
import com.michal.application.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.Test
import java.util.UUID

class MerchantTest {

    private val fixture = AggregateTestFixture(Merchant::class.java)

    @Test
    fun `onboards a new merchant`() {
        fixture
            .`when`(onboardMerchant())
            .expectSuccessfulHandlerExecution()
            .expectEvents(merchantOnboarded())
    }

    @Test
    fun `changes name of an existing merchant`() {
        fixture
            .given(merchantOnboarded())
            .`when`(changeMerchantName())
            .expectSuccessfulHandlerExecution()
            .expectEvents(merchantNameChanged())
    }

    private fun onboardMerchant() = OnboardMerchant(merchantId(), UNITED_STATES_OF_AMERICA, USD)

    private fun merchantOnboarded() = MerchantOnboarded(merchantId(), UNITED_STATES_OF_AMERICA, USD)

    private fun changeMerchantName() = ChangeMerchantName(merchantId(), merchantName())

    private fun merchantNameChanged() = MerchantNameChanged(merchantId(), merchantName())

    private fun merchantName() = Name.of("Norma Gan")

    private fun merchantId() = Id.of(UUID.fromString("9cbf676b-552b-460d-8da4-029e97ca95b7"))
}

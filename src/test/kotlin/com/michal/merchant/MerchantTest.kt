package com.michal.merchant

import com.michal.merchant.domain.Merchant
import com.michal.merchant.domain.command.MerchantCommand.OnboardMerchant
import com.michal.merchant.domain.command.MerchantCommand.SubmitPiiData
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import com.michal.sharedkernel.valueobject.Country.Companion.GERMANY
import com.michal.sharedkernel.valueobject.Country.Companion.UNITED_STATES_OF_AMERICA
import org.axonframework.extension.kotlin.test.aggregateTestFixture
import org.axonframework.extension.kotlin.test.whenever
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MerchantTest {

    private val fixture = aggregateTestFixture<Merchant>()

    @Nested
    inner class OnboardMerchantTest {

        @Test
        fun `onboards a new merchant`() {
            fixture
                .whenever(OnboardMerchant.validStable())
                .expectSuccessfulHandlerExecution()
                .expectEvents(MerchantOnboarded.validStable())
        }
    }

    @Nested
    inner class SubmitPiiDataTest {

        @Test
        fun `submits PII data for an existing merchant`() {
            fixture
                .given(MerchantOnboarded.validStable())
                .whenever(SubmitPiiData.validStable())
                .expectSuccessfulHandlerExecution()
                .expectEvents(PiiDataSubmitted.validStable())
        }

        @Test
        fun `fails when submitting PII data with country different than anonymized data's country`() {
            fixture
                .given(MerchantOnboarded.validStable(UNITED_STATES_OF_AMERICA))
                .whenever(SubmitPiiData.validStable(GERMANY))
                .expectException(IllegalArgumentException::class.java)
                .expectExceptionMessage(
                    "Submitted PII data has different country (DEU) than anonymized data (USA)"
                )
                .expectNoEvents()
        }

        @Test
        fun `submits new version of PII data`() {
            fixture
                .given(
                    MerchantOnboarded.validStable(),
                    PiiDataSubmitted.validStable()
                )
                .whenever(SubmitPiiData.validStableV2())
                .expectSuccessfulHandlerExecution()
                .expectEvents(PiiDataSubmitted.validStableV2())
        }
    }
}

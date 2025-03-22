package com.michal.merchant

import com.michal.merchant.domain.Merchant
import com.michal.merchant.domain.command.MerchantCommand.OnboardMerchant
import com.michal.merchant.domain.command.MerchantCommand.ReconcilePiiData
import com.michal.merchant.domain.command.MerchantCommand.SubmitPiiData
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataReconciled
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

    @Nested
    inner class ReconcilePiiDataTest {

        @Test
        fun `fails to reconcile PII data when there are no PII data submitted yet`() {
            fixture
                .given(MerchantOnboarded.validStable())
                .whenever(ReconcilePiiData.validStable())
                .expectException(IllegalArgumentException::class.java)
                .expectExceptionMessage(
                    "PII data in version 1 and 2 cannot be reconciled because the version 1 of PII " +
                            "data has not been submitted yet"
                )
                .expectNoEvents()
        }

        @Test
        fun `fails to reconcile PII data when there are no PII data in versions that match the reconciliation versions`() {
            fixture
                .given(
                    MerchantOnboarded.validStable(),
                    PiiDataSubmitted.validStable(),
                )
                .whenever(ReconcilePiiData.validStable())
                .expectException(IllegalArgumentException::class.java)
                .expectExceptionMessage(
                    "PII data in version 1 and 2 cannot be reconciled because " +
                            "the version 2 of PII data has not been submitted yet"
                )
                .expectNoEvents()
        }

        @Test
        fun `fails to reconcile PII data when PII data in these versions were already reconciled`() {
            fixture
                .given(
                    MerchantOnboarded.validStable(),
                    PiiDataSubmitted.validStable(),
                    PiiDataSubmitted.validStableV2(),
                    PiiDataReconciled.validStable()
                )
                .whenever(ReconcilePiiData.validStable())
                .expectException(IllegalArgumentException::class.java)
                .expectExceptionMessage(
                    "PII data in version 1 and 2 cannot be reconciled because " +
                            "these versions are already reconciled"
                )
                .expectNoEvents()
        }

        @Test
        fun `fails to reconcile PII data when there is previous version of PII data that is not yet reconciled`() {
            fixture
                .given(
                    MerchantOnboarded.validStable(),
                    PiiDataSubmitted.validStable(),
                    PiiDataSubmitted.validStableV2(),
                    PiiDataSubmitted.validStableV3(),
                )
                .whenever(ReconcilePiiData.validStableV2())
                .expectException(IllegalArgumentException::class.java)
                .expectExceptionMessage(
                    "PII data in version 2 and 3 cannot be reconciled because " +
                            "there is a previous version 1 that must be reconciled first"
                )
                .expectNoEvents()
        }

        @Test
        fun `reconciles PII data when there is single set of PII data submitted`() {
            fixture
                .given(
                    MerchantOnboarded.validStable(),
                    PiiDataSubmitted.validStable(),
                    PiiDataSubmitted.validStableV2()
                )
                .whenever(ReconcilePiiData.validStable())
                .expectSuccessfulHandlerExecution()
                .expectEvents(PiiDataReconciled.validStable())
        }

        @Test
        fun `reconciles PII data when there is more than one set of PII data submitted awaiting reconciliation`() {
            fixture
                .given(
                    MerchantOnboarded.validStable(),
                    PiiDataSubmitted.validStable(),
                    PiiDataSubmitted.validStableV2(),
                    PiiDataSubmitted.validStableV3(),
                )
                .whenever(ReconcilePiiData.validStable())
                .expectSuccessfulHandlerExecution()
                .expectEvents(PiiDataReconciled.validStable())
        }
    }
}

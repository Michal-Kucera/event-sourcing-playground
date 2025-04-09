package com.michal.merchant

import com.michal.config.E2eTest
import com.michal.config.events.readEvents
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataReconciled
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import com.michal.merchant.domain.event.MerchantEvent.WelcomeEmailSent
import com.michal.merchant.domain.valueobject.MerchantId
import io.kotest.matchers.shouldBe
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.junit.jupiter.api.Test

@E2eTest(MerchantE2eClient::class)
class MerchantE2eTest(
    private val merchantE2eClient: MerchantE2eClient,
    private val eventStorageEngine: EventStorageEngine,
) {

    @Test
    fun `merchant lifecycle e2e test`() = with(merchantE2eClient) {
        getsEmptyResponseWhenFetchingEmptyMerchants()
        gets404WhenFetchMerchantByIdThatDoesNotExist()

        canOnboardMerchant()

        gets404WhenFetchingPiiDataVersionThatDoesNotExist(version = 1)
        canSubmitPiiDataInVersion1()
        await untilAsserted { canFetchPiiDataInVersion1() }

        gets404WhenFetchingPiiDataVersionThatDoesNotExist(version = 2)
        canSubmitPiiDataInVersion2()
        await untilAsserted { canFetchPiiDataInVersion2() }

        canReconcilePiiDataInVersion1And2()

        gets404WhenFetchingPiiDataVersionThatDoesNotExist(version = 3)
        canSubmitPiiDataInVersion3()
        await untilAsserted { canFetchPiiDataInVersion3() }

        canReconcilePiiDataInVersion2And3()

        verifyEventStoreContainsAllEvents()

        await untilAsserted { canFetchMerchants() }
        await untilAsserted { canFetchMerchantById() }
    }

    private fun verifyEventStoreContainsAllEvents() {
        eventStorageEngine.readEvents(MerchantId.validStable().value) shouldBe listOf(
            MerchantOnboarded.validStable(),
            PiiDataSubmitted.validStable(),
            WelcomeEmailSent.validStable(),
            PiiDataSubmitted.validStableV2(),
            PiiDataReconciled.validStable(),
            PiiDataSubmitted.validStableV3(),
            PiiDataReconciled.validStableV2(),
        )
    }
}

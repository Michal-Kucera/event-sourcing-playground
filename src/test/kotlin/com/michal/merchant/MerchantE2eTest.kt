package com.michal.merchant

import com.michal.config.E2eTest
import com.michal.config.events.readEvents
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataReconciled
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
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
    fun `merchant lifecycle e2e test`() {
        merchantE2eClient.fetchEmptyMerchants()
        merchantE2eClient.fetchMerchantByIdFailsWith404()

        merchantE2eClient.onboardMerchant()
        merchantE2eClient.submitPiiDataV1()
        merchantE2eClient.submitPiiDataV2()
        merchantE2eClient.reconcilePiiDataV1AndV2()
        merchantE2eClient.submitPiiDataV3()
        merchantE2eClient.reconcilePiiDataV2AndV3()

        eventStorageEngine.readEvents(MerchantId.validStable().value) shouldBe listOf(
            MerchantOnboarded.validStable(),
            PiiDataSubmitted.validStable(),
            PiiDataSubmitted.validStableV2(),
            PiiDataReconciled.validStable(),
            PiiDataSubmitted.validStableV3(),
            PiiDataReconciled.validStableV2(),
        )

        await untilAsserted { merchantE2eClient.fetchMerchants() }
        await untilAsserted { merchantE2eClient.fetchMerchantById() }
    }
}

package com.michal.merchant

import com.michal.config.E2eTest
import com.michal.config.events.readEvents
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import com.michal.merchant.domain.valueobject.MerchantId
import io.kotest.matchers.shouldBe
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.junit.jupiter.api.Test

@E2eTest(MerchantE2eClient::class)
class MerchantE2eTest(
    private val merchantE2eClient: MerchantE2eClient,
    private val eventStorageEngine: EventStorageEngine,
) {

    @Test
    fun `merchant lifecycle e2e test`() {
        merchantE2eClient.onboardMerchant()
        merchantE2eClient.submitPiiData()
        merchantE2eClient.submitPiiDataV2()

        eventStorageEngine.readEvents(MerchantId.validStable().value) shouldBe listOf(
            MerchantOnboarded.validStable(),
            PiiDataSubmitted.validStable(),
            PiiDataSubmitted.validStableV2()
        )
    }
}

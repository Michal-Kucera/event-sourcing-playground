package com.michal.merchant

import com.michal.config.EventSourcingApplication
import com.michal.config.TestcontainersConfiguration
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import com.michal.merchant.domain.valueobject.MerchantId
import io.kotest.matchers.shouldBe
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@Import(TestcontainersConfiguration::class)
@SpringBootTest(classes = [EventSourcingApplication::class], webEnvironment = RANDOM_PORT)
@TestConstructor(autowireMode = ALL)
@AutoConfigureMockMvc
class MerchantE2eTest(
    private val mockMvc: MockMvc,
    private val eventStorageEngine: EventStorageEngine
) {

    @Test
    fun `merchant lifecycle e2e test`() {
        onboardMerchant()
        submitPiiData()

        readEventsForMerchant() shouldBe listOf(
            MerchantOnboarded.validStable(),
            PiiDataSubmitted.validStable()
        )
    }

    private fun onboardMerchant() = mockMvc.post("/merchants") {
        contentType = APPLICATION_JSON
        content = """
            {
              "merchantId": "9cbf676b-552b-460d-8da4-029e97ca95b7",
              "platformId": "e6ceecdb-5ad0-454d-a428-f9f0f873f69b",
              "merchantExternalId": "026c516959354797bd1a7bdc03e2e8c4",
              "countryCode": "USA",
              "postCode": "08030",
              "city": "Washington DC",
              "addressLine1": "Trumpstreet",
              "addressLine2": null,
              "currencyCode": "USD",
              "kitchenTypes": [
                "Korean",
                "Asian"
              ]
            }
        """
    }.andExpect { status { isCreated() } }

    private fun submitPiiData() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data") {
        contentType = APPLICATION_JSON
        content = """
            {
              "name": "Paulo Merido",
              "vatNumber": "123456789",
              "registrationNumber": "987654321",
              "countryCode": "USA",
              "postCode": "08030",
              "city": "Washington DC",
              "addressLine1": "Trumpstreet 4",
              "addressLine2": "Block 1"
            }
        """
    }.andExpect { status { isNoContent() } }

    private fun readEventsForMerchant() = eventStorageEngine.readEvents(MerchantId.validStable().toString())
        .asSequence()
        .toList()
        .map { it.payload }
}

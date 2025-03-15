package com.michal.e2e

import com.michal.config.EventSourcingApplication
import com.michal.config.TestcontainersConfiguration
import com.michal.domain.merchant.Merchant
import com.michal.domain.merchant.Merchant.Country.Companion.GERMANY
import com.michal.domain.merchant.Merchant.Currency.Companion.EUR
import com.michal.domain.merchant.Merchant.PiiData
import com.michal.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.domain.merchant.MerchantEvent.PiiDataSubmitted
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
import java.util.UUID

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
            MerchantOnboarded(merchantId(), GERMANY, EUR),
            PiiDataSubmitted(merchantId(), merchantName())
        )
    }

    private fun onboardMerchant() = mockMvc.post("/merchants") {
        contentType = APPLICATION_JSON
        content = """
            {
              "merchantId": "${merchantId()}",
              "countryCode": "DEU",
              "currencyCode": "EUR"
            }
        """
    }.andExpect { status { isCreated() } }

    private fun submitPiiData() = mockMvc.post("/merchants/{merchant-id}/pii-data", merchantId()) {
        contentType = APPLICATION_JSON
        content = """
            {
              "name": "Paulo Merido"
            }
        """
    }.andExpect { status { isNoContent() } }

    private fun readEventsForMerchant() = eventStorageEngine.readEvents(merchantId().toString())
        .asSequence()
        .toList()
        .map { it.payload }

    private fun merchantName() = PiiData.Name.of("Paulo Merido")

    private fun merchantId() = Merchant.Id.of(UUID.fromString("19d32716-b6d8-4e54-b54a-4e44302e0df5"))
}

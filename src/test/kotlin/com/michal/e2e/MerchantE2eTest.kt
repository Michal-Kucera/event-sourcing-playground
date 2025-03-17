package com.michal.e2e

import com.michal.config.EventSourcingApplication
import com.michal.config.TestcontainersConfiguration
import com.michal.domain.merchant.AnonymizedData
import com.michal.domain.merchant.AnonymizedData.KitchenType
import com.michal.domain.merchant.Country.Companion.GERMANY
import com.michal.domain.merchant.Currency.Companion.EUR
import com.michal.domain.merchant.Id
import com.michal.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.domain.merchant.MerchantEvent.PiiDataSubmitted
import com.michal.domain.merchant.PiiData
import com.michal.domain.merchant.PlatformId
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
            MerchantOnboarded(merchantId(), platformId(), anonymizedAddress(), EUR, kitchenTypes()),
            PiiDataSubmitted(merchantId(), merchantName(), legalEntityId(), address())
        )
    }

    private fun onboardMerchant() = mockMvc.post("/merchants") {
        contentType = APPLICATION_JSON
        content = """
            {
              "merchantId": "19d32716-b6d8-4e54-b54a-4e44302e0df5",
              "platformId": "e6ceecdb-5ad0-454d-a428-f9f0f873f69b",
              "merchantExternalId": "026c516959354797bd1a7bdc03e2e8c4",
              "countryCode": "DEU",
              "postCode": "08030",
              "city": "Berlin",
              "addressLine1": "Karlstrasse",
              "addressLine2": "N/A",
              "currencyCode": "EUR",
              "kitchenTypes": [
                "Asian",
                "Korean"
              ]
            }
        """
    }.andExpect { status { isCreated() } }

    private fun submitPiiData() = mockMvc.post("/merchants/19d32716-b6d8-4e54-b54a-4e44302e0df5/pii-data") {
        contentType = APPLICATION_JSON
        content = """
            {
              "name": "Paulo Merido",
              "vatNumber": "123456789",
              "registrationNumber": "987654321",
              "countryCode": "DEU",
              "postCode": "08030",
              "city": "Berlin",
              "addressLine1": "Karlstrasse 7",
              "addressLine2": "Block 3"
            }
        """
    }.andExpect { status { isNoContent() } }

    private fun readEventsForMerchant() = eventStorageEngine.readEvents(merchantId().toString())
        .asSequence()
        .toList()
        .map { it.payload }

    private fun merchantName() = PiiData.Name.of("Paulo Merido")

    private fun legalEntityId() = PiiData.LegalEntityId.of(
        country = GERMANY,
        vatNumber = "123456789",
        registrationNumber = "987654321"
    )

    private fun platformId() = PlatformId.of(
        platformId = UUID.fromString("e6ceecdb-5ad0-454d-a428-f9f0f873f69b"),
        merchantExternalId = "026c516959354797bd1a7bdc03e2e8c4"
    )

    private fun anonymizedAddress() = AnonymizedData.LegalAddress.of(
        country = GERMANY,
        postCode = "08030",
        city = "Berlin",
        addressLine1 = "Karlstrasse",
        addressLine2 = "N/A"
    )

    private fun kitchenTypes() = setOf(KitchenType.of("Asian"), KitchenType.of("Korean"))

    private fun address() = PiiData.LegalAddress.of(
        country = GERMANY,
        postCode = "08030",
        city = "Berlin",
        addressLine1 = "Karlstrasse 7",
        addressLine2 = "Block 3"
    )

    private fun merchantId() = Id.of(UUID.fromString("19d32716-b6d8-4e54-b54a-4e44302e0df5"))
}

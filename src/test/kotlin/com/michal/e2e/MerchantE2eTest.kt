package com.michal.e2e

import com.michal.application.domain.merchant.Merchant
import com.michal.application.domain.merchant.Merchant.Country.Companion.GERMANY
import com.michal.application.domain.merchant.Merchant.Currency.Companion.EUR
import com.michal.config.EventSourcingApplication
import com.michal.config.TestcontainersConfiguration
import io.kotest.matchers.shouldBe
import org.axonframework.modelling.command.Repository
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
    private val merchantRepository: Repository<Merchant>
) {

    @Test
    fun `merchant lifecycle e2e test`() {
        mockMvc.post("/merchants") {
            contentType = APPLICATION_JSON
            content = """
                {
                  "merchantId": "${merchantId()}",
                  "countryCode": "DEU",
                  "currencyCode": "EUR"
                }
            """
        }.andExpect {
            status { isCreated() }
        }

        merchantRepository.load(merchantId()).execute {
            it.aggregateId shouldBe Merchant.Id.of(UUID.fromString(merchantId()))
            it.country shouldBe GERMANY
            it.currency shouldBe EUR
            it.name shouldBe null
        }
    }

    private fun merchantId() = "19d32716-b6d8-4e54-b54a-4e44302e0df5"
}

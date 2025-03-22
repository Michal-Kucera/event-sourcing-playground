package com.michal.merchant

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

class MerchantE2eClient(private val mockMvc: MockMvc) {

    fun onboardMerchant() = mockMvc.post("/merchants") {
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

    fun submitPiiData() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data") {
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

    fun submitPiiDataV2() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data") {
        contentType = APPLICATION_JSON
        content = """
            {
              "name": "Jennifer Shinde",
              "vatNumber": "45678901",
              "registrationNumber": "76540987",
              "countryCode": "USA",
              "postCode": "08031",
              "city": "Chicago",
              "addressLine1": "Cali 123",
              "addressLine2": null
            }
        """
    }.andExpect { status { isNoContent() } }
}

package com.michal.merchant

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.json.JsonCompareMode.STRICT
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
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

    fun submitPiiDataV1() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data") {
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

    fun submitPiiDataV3() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data") {
        contentType = APPLICATION_JSON
        content = """
            {
              "name": "Mona Chauhan",
              "vatNumber": "98754567",
              "registrationNumber": "345678987",
              "countryCode": "USA",
              "postCode": "08032",
              "city": "San Francisco",
              "addressLine1": "Sesame street 33",
              "addressLine2": "2nd floor"
            }
        """
    }.andExpect { status { isNoContent() } }

    fun reconcilePiiDataV1AndV2() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data/reconcile") {
        contentType = APPLICATION_JSON
        content = """
            {
              "olderVersion": 1,
              "newerVersion": 2,
              "reconciledName": "Jennifer Shinde",
              "reconciledVatNumber": "45678901",
              "reconciledRegistrationNumber": "76540987",
              "reconciledCountryCode": "USA",
              "reconciledPostCode": "08031",
              "reconciledCity": "Chicago",
              "reconciledAddressLine1": "Cali 123",
              "reconciledAddressLine2": null
            }
        """
    }.andExpect { status { isNoContent() } }

    fun reconcilePiiDataV2AndV3() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data/reconcile") {
        contentType = APPLICATION_JSON
        content = """
            {
              "olderVersion": 2,
              "newerVersion": 3,
              "reconciledName": "Jennifer Shinde",
              "reconciledVatNumber": "45678901",
              "reconciledRegistrationNumber": "76540987",
              "reconciledCountryCode": "USA",
              "reconciledPostCode": "08032",
              "reconciledCity": "San Francisco",
              "reconciledAddressLine1": "Sesame street 33",
              "reconciledAddressLine2": "2nd floor"
            }
        """
    }.andExpect { status { isNoContent() } }

    fun fetchEmptyMerchants() = mockMvc.get("/merchants") {
        accept = APPLICATION_JSON
    }.andExpect {
        content {
            contentType(APPLICATION_JSON)
            json("[]", STRICT)
        }
        status { isOk() }
    }

    fun fetchMerchants() = mockMvc.get("/merchants") {
        accept = APPLICATION_JSON
    }.andExpect {
        content {
            contentType(APPLICATION_JSON)
            json(
                """
                    [
                      {
                        "id": "9cbf676b-552b-460d-8da4-029e97ca95b7",
                        "name": "Jennifer Shinde",
                        "platformId": "e6ceecdb-5ad0-454d-a428-f9f0f873f69b",
                        "externalId": "026c516959354797bd1a7bdc03e2e8c4",
                        "countryCode": "USA",
                        "postCode": "08032",
                        "city":  "San Francisco",
                        "addressLine1": "Sesame street 33",
                        "addressLine2":  "2nd floor",
                        "currencyCode": "USD",
                        "kitchenTypes": [
                          "Korean",
                          "Asian"
                        ],
                        "vatNumber": "45678901",
                        "registrationNumber":  "76540987",
                        "latestSubmittedPiiDataVersion": 3,
                        "latestReconciledPiiDataVersion":  3
                      }
                    ]
                """,
                STRICT
            )
        }
        status { isOk() }
    }

    fun fetchMerchantByIdFailsWith404() = mockMvc.get("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7") {
        accept = APPLICATION_JSON
    }.andExpect { status { isNotFound() } }

    fun fetchMerchantById() = mockMvc.get("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7") {
        accept = APPLICATION_JSON
    }.andExpect {
        content {
            contentType(APPLICATION_JSON)
            json(
                """
                    {
                      "id": "9cbf676b-552b-460d-8da4-029e97ca95b7",
                      "name": "Jennifer Shinde",
                      "platformId": "e6ceecdb-5ad0-454d-a428-f9f0f873f69b",
                      "externalId": "026c516959354797bd1a7bdc03e2e8c4",
                      "countryCode": "USA",
                      "postCode": "08032",
                      "city":  "San Francisco",
                      "addressLine1": "Sesame street 33",
                      "addressLine2":  "2nd floor",
                      "currencyCode": "USD",
                      "kitchenTypes": [
                        "Korean",
                        "Asian"
                      ],
                      "vatNumber": "45678901",
                      "registrationNumber":  "76540987",
                      "latestSubmittedPiiDataVersion": 3,
                      "latestReconciledPiiDataVersion":  3
                    }
                """,
                STRICT
            )
        }
        status { isOk() }
        header { string("X-LATEST-SEQUENCE-NUMBER", "5") }
    }
}

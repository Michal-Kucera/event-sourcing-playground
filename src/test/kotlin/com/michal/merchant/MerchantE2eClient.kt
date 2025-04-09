package com.michal.merchant

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.json.JsonCompareMode.STRICT
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class MerchantE2eClient(private val mockMvc: MockMvc) {

    fun canOnboardMerchant() = mockMvc.post("/merchants") {
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

    fun canSubmitPiiDataInVersion1() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data") {
        contentType = APPLICATION_JSON
        content = """
            {
              "name": "Paulo Merido",
              "email": "paulo.merido@hello.com",
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

    fun canSubmitPiiDataInVersion2() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data") {
        contentType = APPLICATION_JSON
        content = """
            {
              "name": "Jennifer Shinde",
              "email": "jennifer.shinde@hello.com",
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

    fun canSubmitPiiDataInVersion3() = mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data") {
        contentType = APPLICATION_JSON
        content = """
            {
              "name": "Mona Chauhan",
              "email": "mona.chauhan@hello.com",
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

    fun canReconcilePiiDataInVersion1And2() =
        mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data/reconcile") {
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

    fun canReconcilePiiDataInVersion2And3() =
        mockMvc.post("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data/reconcile") {
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

    fun getsEmptyResponseWhenFetchingEmptyMerchants() = mockMvc.get("/merchants") {
        accept = APPLICATION_JSON
    }.andExpect {
        content {
            contentType(APPLICATION_JSON)
            json("[]", STRICT)
        }
        status { isOk() }
    }

    fun canFetchMerchants() = mockMvc.get("/merchants") {
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
                        "email": "paulo.merido@hello.com",
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

    fun gets404WhenFetchingPiiDataVersionThatDoesNotExist(version: Int) =
        mockMvc.get("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data/$version") {
            accept = APPLICATION_JSON
        }.andExpect { status { isNotFound() } }

    fun canFetchPiiDataInVersion1() = mockMvc.get("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data/1") {
        accept = APPLICATION_JSON
    }.andExpect {
        content {
            contentType(APPLICATION_JSON)
            json(
                """
                  {
                    "merchantId": "9cbf676b-552b-460d-8da4-029e97ca95b7",
                    "version": 1,
                    "name": "Paulo Merido",
                    "email": "paulo.merido@hello.com",
                    "vatNumber": "123456789",
                    "registrationNumber": "987654321",
                    "countryCode": "USA",
                    "postCode": "08030",
                    "city": "Washington DC",
                    "addressLine1": "Trumpstreet 4",
                    "addressLine2": "Block 1"
                  }
                """,
                STRICT
            )
        }
        status { isOk() }
    }

    fun canFetchPiiDataInVersion2() = mockMvc.get("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data/2") {
        accept = APPLICATION_JSON
    }.andExpect {
        content {
            contentType(APPLICATION_JSON)
            json(
                """
                  {
                    "merchantId": "9cbf676b-552b-460d-8da4-029e97ca95b7",
                    "version": 2,
                    "name": "Jennifer Shinde",
                    "email": "jennifer.shinde@hello.com",
                    "vatNumber": "45678901",
                    "registrationNumber": "76540987",
                    "countryCode": "USA",
                    "postCode": "08031",
                    "city": "Chicago",
                    "addressLine1": "Cali 123",
                    "addressLine2": null
                  }
                """,
                STRICT
            )
        }
        status { isOk() }
    }

    fun canFetchPiiDataInVersion3() = mockMvc.get("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7/pii-data/3") {
        accept = APPLICATION_JSON
    }.andExpect {
        content {
            contentType(APPLICATION_JSON)
            json(
                """
                  {
                    "merchantId": "9cbf676b-552b-460d-8da4-029e97ca95b7",
                    "version": 3,
                    "name": "Mona Chauhan",
                    "email": "mona.chauhan@hello.com",
                    "vatNumber": "98754567",
                    "registrationNumber": "345678987",
                    "countryCode": "USA",
                    "postCode": "08032",
                    "city": "San Francisco",
                    "addressLine1": "Sesame street 33",
                    "addressLine2": "2nd floor"
                  }
                """,
                STRICT
            )
        }
        status { isOk() }
    }

    fun gets404WhenFetchMerchantByIdThatDoesNotExist() =
        mockMvc.get("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7") {
            accept = APPLICATION_JSON
        }.andExpect { status { isNotFound() } }

    fun canFetchMerchantById() = mockMvc.get("/merchants/9cbf676b-552b-460d-8da4-029e97ca95b7") {
        accept = APPLICATION_JSON
    }.andExpect {
        content {
            contentType(APPLICATION_JSON)
            json(
                """
                    {
                      "id": "9cbf676b-552b-460d-8da4-029e97ca95b7",
                      "name": "Jennifer Shinde",
                      "email": "paulo.merido@hello.com",
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
        header { string("X-LATEST-SEQUENCE-NUMBER", "6") }
    }
}

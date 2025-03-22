package com.michal.merchant.domain.valueobject

import com.michal.sharedkernel.valueobject.Country

data class PiiData private constructor(
    val name: Name,
    val legalEntityId: LegalEntityId,
    val legalAddress: LegalAddress
) {
    companion object {
        fun with(
            name: Name,
            legalEntityId: LegalEntityId,
            legalAddress: LegalAddress
        ): PiiData {
            require(legalEntityId.country == legalAddress.country) {
                "Legal entity has different country (${legalEntityId.country}) " +
                        "than legal address (${legalAddress.country})"
            }
            return PiiData(name, legalEntityId, legalAddress)
        }
    }

    data class Name private constructor(
        val value: String
    ) {
        override fun toString(): String = value

        companion object {
            fun of(name: String): Name {
                require(name.isNotBlank()) { "Name cannot be blank" }
                return Name(name)
            }
        }
    }

    data class LegalEntityId private constructor(
        val country: Country,
        val vatNumber: String?,
        val registrationNumber: String?
    ) {

        companion object {
            fun of(
                country: Country,
                vatNumber: String?,
                registrationNumber: String?
            ): LegalEntityId {
                require(!vatNumber.isNullOrBlank() || !registrationNumber.isNullOrBlank()) {
                    "At least one of VAT number or registration number must be provided"
                }
                return LegalEntityId(country, vatNumber, registrationNumber)
            }
        }
    }

    data class LegalAddress private constructor(
        val country: Country,
        val postCode: String,
        val city: String,
        val addressLine1: String,
        val addressLine2: String?,
    ) {
        companion object {
            fun of(
                country: Country,
                postCode: String,
                city: String,
                addressLine1: String,
                addressLine2: String?,
            ) = LegalAddress(country, postCode, city, addressLine1, addressLine2)
        }
    }
}

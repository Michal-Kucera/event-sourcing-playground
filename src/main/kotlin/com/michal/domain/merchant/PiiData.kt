package com.michal.domain.merchant

data class PiiData private constructor(
    val name: Name,
    val legalEntityIdentifiers: LegalEntityIdentifiers,
    val address: Address
) {
    companion object {
        fun with(
            name: Name,
            legalEntityIdentifiers: LegalEntityIdentifiers,
            address: Address
        ): PiiData = PiiData(name, legalEntityIdentifiers, address)
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

    data class LegalEntityIdentifiers private constructor(
        val vatNumber: String?,
        val registrationNumber: String?
    ) {

        companion object {
            fun of(
                vatNumber: String?,
                registrationNumber: String?
            ): LegalEntityIdentifiers {
                require(!vatNumber.isNullOrBlank() || !registrationNumber.isNullOrBlank()) {
                    "At least one of VAT number or registration number must be provided"
                }
                return LegalEntityIdentifiers(vatNumber, registrationNumber)
            }
        }
    }

    data class Address private constructor(
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
            ): Address = Address(country, postCode, city, addressLine1, addressLine2)
        }
    }
}

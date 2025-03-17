package com.michal.domain.merchant

data class AnonymizedData private constructor(
    val legalAddress: LegalAddress,
    val currency: Currency,
) {
    companion object {
        fun create(legalAddress: LegalAddress, currency: Currency) = AnonymizedData(legalAddress, currency)
    }

    data class LegalAddress private constructor(
        val country: Country,
        val postCode: String?,
        val city: String?,
        val addressLine1: String?,
        val addressLine2: String?,
    ) {
        companion object {
            fun of(
                country: Country,
                postCode: String?,
                city: String?,
                addressLine1: String?,
                addressLine2: String?,
            ) = LegalAddress(country, postCode, city, addressLine1, addressLine2)
        }
    }
}

package com.michal.domain.merchant

data class AnonymizedData private constructor(
    val legalAddress: LegalAddress,
    val currency: Currency,
    val kitchenTypes: Set<KitchenType>
) {
    companion object {
        fun create(legalAddress: LegalAddress, currency: Currency, kitchenTypes: Set<KitchenType>): AnonymizedData {
            require(kitchenTypes.isNotEmpty()) { "At least one kitchen type must be provided" }
            return AnonymizedData(legalAddress, currency, kitchenTypes)
        }
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

    data class KitchenType private constructor(
        val value: String
    ) {
        override fun toString(): String = value

        companion object {
            fun of(value: String) = KitchenType(value)
        }
    }
}

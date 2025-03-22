package com.michal.merchant.domain.valueobject

import com.michal.sharedkernel.valueobject.Country

data class AnonymizedData private constructor(
    val legalAddress: LegalAddress,
    val kitchenTypes: Set<KitchenType>
) {
    fun hasSame(country: Country) = country == legalAddress.country

    companion object {
        fun with(legalAddress: LegalAddress, kitchenTypes: Set<KitchenType>): AnonymizedData {
            require(kitchenTypes.isNotEmpty()) { "At least one kitchen type must be provided" }
            return AnonymizedData(legalAddress, kitchenTypes)
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

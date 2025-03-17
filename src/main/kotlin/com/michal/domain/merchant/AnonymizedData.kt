package com.michal.domain.merchant

data class AnonymizedData private constructor(
    val country: Country,
    val currency: Currency,
) {
    companion object {
        fun create(country: Country, currency: Currency) = AnonymizedData(country, currency)
    }
}

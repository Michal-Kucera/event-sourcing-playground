package com.michal.sharedkernel.valueobject

data class Currency private constructor(val code: java.util.Currency) {
    override fun toString(): String = code.displayName

    companion object {
        fun from(currencyCode: String): Currency = Currency(java.util.Currency.getInstance(currencyCode))

        val EUR: Currency = from("EUR")
        val USD: Currency = from("USD")
    }
}

package com.michal.domain.merchant

data class Country private constructor(val code: String) {
    override fun toString(): String = code

    companion object {
        fun from(countryCode: String): Country {
            require(countryCode.trim().length == 3) { "Country code must be in ISO-3 format" }
            return Country(countryCode)
        }

        val GERMANY: Country = from("DEU")
        val UNITED_STATES_OF_AMERICA: Country = from("USA")
    }
}

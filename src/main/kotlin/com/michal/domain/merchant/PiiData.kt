package com.michal.domain.merchant

data class PiiData private constructor(
    val name: Name,
    val legalEntityIdentifiers: LegalEntityIdentifiers
) {
    companion object {
        fun with(
            name: Name,
            legalEntityIdentifiers: LegalEntityIdentifiers
        ): PiiData = PiiData(name, legalEntityIdentifiers)
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
}

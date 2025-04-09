package com.michal.sharedkernel.valueobject

data class Email private constructor(
    val value: String
) {
    override fun toString(): String = value

    companion object {
        fun of(email: String): Email {
            require("@" in email) { "Email must contain @" }
            return Email(email)
        }

        fun unknown() = Email("N/A")
    }
}

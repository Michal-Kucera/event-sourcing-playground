package com.michal.merchant.domain.valueobject

import java.util.UUID

data class MerchantId private constructor(
    val value: UUID
) {
    override fun toString(): String = value.toString()

    companion object {
        fun of(id: UUID): MerchantId = MerchantId(id)

        fun of(id: String): MerchantId = of(UUID.fromString(id))
    }
}

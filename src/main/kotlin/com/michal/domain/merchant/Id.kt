package com.michal.domain.merchant

import java.util.UUID

data class Id private constructor(
    val value: UUID
) {
    override fun toString(): String = value.toString()

    companion object {
        fun of(id: UUID): Id = Id(id)
    }
}

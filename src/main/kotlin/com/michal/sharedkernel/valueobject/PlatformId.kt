package com.michal.sharedkernel.valueobject

import java.util.UUID

data class PlatformId private constructor(
    val platformId: UUID,
    val merchantExternalId: String
) {

    companion object {
        fun of(platformId: UUID, merchantExternalId: String) = PlatformId(platformId, merchantExternalId)

        fun of(platformId: String, merchantExternalId: String) = of(UUID.fromString(platformId), merchantExternalId)
    }
}

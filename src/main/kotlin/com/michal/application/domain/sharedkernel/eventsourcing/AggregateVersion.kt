package com.michal.application.domain.sharedkernel.eventsourcing

data class AggregateVersion private constructor(
    val value: Int,
) {
    fun increaseBy(value: Int): AggregateVersion = of(this.value + value)

    companion object {
        fun initialVersion() = AggregateVersion(1)

        fun of(value: Int): AggregateVersion = AggregateVersion(value)
    }
}

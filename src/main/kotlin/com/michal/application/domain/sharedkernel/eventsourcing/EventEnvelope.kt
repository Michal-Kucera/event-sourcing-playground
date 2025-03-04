package com.michal.application.domain.sharedkernel.eventsourcing

import java.time.LocalDateTime
import java.util.UUID

data class EventEnvelope private constructor(
    val id: Id,
    val streamId: StreamId,
    val payload: Event<out Any>,
    val metadata: Metadata,
    val version: AggregateVersion,
    val createdAt: LocalDateTime
) {

    companion object {
        fun wrap(
            id: Id,
            streamId: StreamId,
            payload: Event<out Any>,
            metadata: Metadata,
            version: AggregateVersion,
            createdAt: LocalDateTime
        ): EventEnvelope = EventEnvelope(id, streamId, payload, metadata, version, createdAt)
    }

    data class Id private constructor(val value: UUID) {
        companion object {
            fun of(value: UUID): Id = Id(value)
        }
    }

    data class StreamId private constructor(val value: String) {
        companion object {
            fun of(value: String): StreamId = StreamId(value)
        }
    }

    data class Metadata private constructor(
        //val triggeredBy: String,
        val eventType: String,
        // val tracingContext: TracingContext
    ) {
        companion object {
            fun of(eventType: String): Metadata = Metadata(eventType)
        }
    }
}

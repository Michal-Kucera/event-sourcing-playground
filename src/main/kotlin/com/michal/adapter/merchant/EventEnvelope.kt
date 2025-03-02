package com.michal.adapter.merchant

import com.michal.application.domain.sharedkernel.eventsourcing.Event
import java.time.LocalDateTime
import java.util.UUID

data class EventEnvelope private constructor(
    val id: Id,
    val streamId: StreamId,
    val payload: Event,
    val metadata: Metadata,
    val version: Int,
    val createdAt: LocalDateTime
) {

    companion object {
        fun wrap(
            id: Id,
            streamId: StreamId,
            payload: Event,
            metadata: Metadata,
            version: Int,
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

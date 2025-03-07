package com.michal.readevents

import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class ReadEventsResource(
    private val eventStorageEngine: EventStorageEngine
) {

    @GetMapping("/internal/events/{aggregateId}")
    fun resolveEvents(
        @PathVariable("aggregateId") aggregateId: UUID
    ) = eventStorageEngine.readEvents(aggregateId.toString()).asSequence().toList()
}

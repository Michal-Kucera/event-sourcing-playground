package com.michal.config.events

import org.axonframework.eventsourcing.eventstore.EventStore
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class EventsResource(
    private val eventStore: EventStore
) {

    @GetMapping("/internal/events/{aggregate-name}/{aggregate-id}")
    @ResponseStatus(OK)
    fun findEventsForAggregate(
        @PathVariable("aggregate-name") aggregateName: String,
        @PathVariable("aggregate-id") aggregateId: UUID,
    ) = eventStore.readEvents(aggregateId.toString())
        .asSequence()
        .filter { it.type.equals(aggregateName, ignoreCase = true) }
        .toList()
}

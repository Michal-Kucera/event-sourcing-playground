package com.michal.application.domain.sharedkernel.eventsourcing

open class EventSourcedAggregate<ID>(
    private val streamName: String,
    private val aggregateId: ID,
) {

    private val events = mutableListOf<Event>()

    fun unpublishedEvents(): List<Event> {
        val unpublishedEvents = events.toList()
        events.clear()
        return unpublishedEvents
    }

    fun append(event: Event) {
        events += event
    }
}

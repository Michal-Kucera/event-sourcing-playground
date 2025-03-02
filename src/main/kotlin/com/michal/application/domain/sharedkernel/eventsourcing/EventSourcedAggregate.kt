package com.michal.application.domain.sharedkernel.eventsourcing

open class EventSourcedAggregate<ID, EVENT_TYPE : Event>(
    private val aggregateId: ID,
) {

    private val events = mutableListOf<EVENT_TYPE>()

    fun unpublishedEvents(): List<EVENT_TYPE> {
        val unpublishedEvents = events.toList()
        events.clear()
        return unpublishedEvents
    }

    fun append(event: EVENT_TYPE) {
        events += event
    }
}

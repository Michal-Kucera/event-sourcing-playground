package com.michal.application.domain.sharedkernel.eventsourcing

open class EventSourcedAggregate<ID, EVENT : Event<ID>>(
    open val aggregateId: ID,
) {

    private val events = mutableListOf<EVENT>()

    fun unpublishedEvents(): List<EVENT> {
        val unpublishedEvents = events.toList()
        events.clear()
        return unpublishedEvents
    }

    fun append(event: EVENT) {
        events += event
    }
}

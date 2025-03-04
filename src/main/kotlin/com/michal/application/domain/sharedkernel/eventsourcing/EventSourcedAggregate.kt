package com.michal.application.domain.sharedkernel.eventsourcing

open class EventSourcedAggregate<ID, EVENT : Event<ID>>(
    open val aggregateId: ID,
    open val version: AggregateVersion,
    private val eventRegistry: EventRegistry<EVENT> = EventRegistry()
) {

    fun unpublishedEvents(): List<EVENT> = eventRegistry.popEvents()

    fun append(event: EVENT): Unit = eventRegistry.raise(event)
}

package com.michal.application.domain.sharedkernel.eventsourcing

interface EventStore<T : EventSourcedAggregate<ID, out Event<ID>>, ID> {
    fun storeEventsFor(aggregate: T)

    fun findBy(aggregateId: ID): T?

    fun getBy(aggregateId: ID): T

    class AggregateNotFound(message: String) : RuntimeException(message)
}

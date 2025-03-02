package com.michal.application.domain.sharedkernel.eventsourcing

interface EventStore<T : EventSourcedAggregate<ID>, ID> {
    val streamName: String

    fun storeEventsFor(aggregate: T)

    fun findBy(aggregateId: ID): T?

    fun getBy(aggregateId: ID): T

    class AggregateNotFound(message: String) : RuntimeException(message)
}

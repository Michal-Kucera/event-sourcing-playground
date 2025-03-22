package com.michal.sharedkernel.eventsourcing

interface Event<ID> {
    val aggregateId: ID
}

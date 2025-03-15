package com.michal.domain.sharedkernel.eventsourcing

interface Event<ID> {
    val aggregateId: ID
}

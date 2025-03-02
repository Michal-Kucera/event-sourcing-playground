package com.michal.application.domain.sharedkernel.eventsourcing

interface Event<ID> {
    val aggregateId: ID
}

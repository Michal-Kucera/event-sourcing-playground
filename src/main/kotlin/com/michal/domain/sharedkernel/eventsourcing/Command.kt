package com.michal.domain.sharedkernel.eventsourcing

interface Command<ID> {
    val aggregateId: ID
}

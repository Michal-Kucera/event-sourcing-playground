package com.michal.sharedkernel.eventsourcing

interface Command<ID> {
    val aggregateId: ID
}

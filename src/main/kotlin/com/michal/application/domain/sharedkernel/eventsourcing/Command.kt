package com.michal.application.domain.sharedkernel.eventsourcing

interface Command<ID> {
    val aggregateId: ID
}

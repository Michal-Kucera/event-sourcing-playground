package com.michal.sharedkernel.event

interface Command<ID> {
    val aggregateId: ID
}

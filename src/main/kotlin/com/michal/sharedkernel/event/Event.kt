package com.michal.sharedkernel.event

interface Event<ID> {
    val aggregateId: ID
}

package com.michal.config.events

import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import java.util.UUID

fun EventStorageEngine.readEvents(aggregateId: UUID) = readEvents(aggregateId.toString())
    .asSequence()
    .toList()
    .map { it.payload }

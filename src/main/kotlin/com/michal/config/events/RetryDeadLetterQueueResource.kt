package com.michal.config.events

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.deadletter.jpa.JpaDeadLetter
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class RetryDeadLetterQueueResource(
    private val eventProcessingConfiguration: EventProcessingConfiguration
) {

    @PostMapping("/internal/events/dead-letter/{processing-group}/{aggregate-id}/retry")
    @ResponseStatus(OK)
    fun retryDlqForAggregate(
        @PathVariable("processing-group") processingGroup: String,
        @PathVariable("aggregate-id") aggregateId: UUID
    ) = eventProcessingConfiguration.sequencedDeadLetterProcessor(processingGroup)
        .orElseThrow { error("No DLQ configured for processing group $processingGroup") }
        .process { (it as JpaDeadLetter<*>).sequenceIdentifier == aggregateId.toString() }
}

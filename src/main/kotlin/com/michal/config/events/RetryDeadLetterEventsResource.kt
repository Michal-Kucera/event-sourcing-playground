package com.michal.config.events

import org.axonframework.config.EventProcessingConfiguration
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RetryDeadLetterEventsResource(
    private val eventProcessingConfiguration: EventProcessingConfiguration
) {

    @PostMapping("/internal/events/dead-letter/{processing-group}/retry")
    fun retryEvents(
        @PathVariable("processing-group") processingGroup: String
    ) = eventProcessingConfiguration.sequencedDeadLetterProcessor(processingGroup)
        .orElseThrow { error("No DLQ configured for processing group $processingGroup") }
        .processAny()
}

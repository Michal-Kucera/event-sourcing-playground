package com.michal.config.events

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class ReplayEventsResource(
    private val eventProcessingConfiguration: EventProcessingConfiguration
) {

    @PostMapping("/internal/events/{processing-group}/replay")
    @ResponseStatus(NO_CONTENT)
    fun replayEventsForProcessingGroup(
        @PathVariable("processing-group") processingGroup: String,
    ) = eventProcessingConfiguration.eventProcessorByProcessingGroup<TrackingEventProcessor>(processingGroup)
        .orElseThrow { error("No event processor configured for processing group $processingGroup") }
        .apply {
            shutDown()
            resetTokens()
            start()
        }.processingStatus()
}

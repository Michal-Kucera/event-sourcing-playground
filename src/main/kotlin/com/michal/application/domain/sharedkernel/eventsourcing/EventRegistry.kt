package com.michal.application.domain.sharedkernel.eventsourcing

data class EventRegistry<EVENT>(
    private val events: MutableList<EVENT> = mutableListOf()
) {
    fun popEvents(): List<EVENT> {
        val poppedEvents = events.toList()
        events.clear()
        return poppedEvents
    }

    fun raise(event: EVENT) {
        events += event
    }
}

package com.michal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventSourcingApplication

fun main(args: Array<String>) {
    runApplication<EventSourcingApplication>(*args)
}

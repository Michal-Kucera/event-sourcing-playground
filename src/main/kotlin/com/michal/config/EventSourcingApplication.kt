package com.michal.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.michal"])
class EventSourcingApplication

fun main(args: Array<String>) {
    runApplication<EventSourcingApplication>(*args)
}

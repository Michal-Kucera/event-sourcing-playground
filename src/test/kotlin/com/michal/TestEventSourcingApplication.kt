package com.michal

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<EventSourcingApplication>().with(TestcontainersConfiguration::class).run(*args)
}

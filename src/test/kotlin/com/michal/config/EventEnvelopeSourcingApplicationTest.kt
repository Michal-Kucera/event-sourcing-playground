package com.michal.config

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest(classes = [EventSourcingApplication::class], webEnvironment = RANDOM_PORT)
class EventEnvelopeSourcingApplicationTest {

    @Test
    fun contextLoads() {
    }
}

@file:Suppress("RemoveRedundantQualifierName")

package com.michal.adapter.merchant

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.michal.adapter.merchant.EventEnvelope.Metadata
import com.michal.application.domain.merchant.Merchant
import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.MerchantEvent
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.application.domain.merchant.MerchantEventStore
import com.michal.application.domain.sharedkernel.eventsourcing.EventStore.AggregateNotFound
import com.michal.jooq.public.tables.records.EventStoreRecord
import com.michal.jooq.public.tables.references.EVENT_STORE
import org.jooq.DSLContext
import org.jooq.JSONB
import java.time.LocalDateTime
import java.util.UUID

class PostgresMerchantEventStore(
    private val dslContext: DSLContext,
    private val objectMapper: ObjectMapper
) : MerchantEventStore {

    override fun storeEventsFor(aggregate: Merchant) {
        var latestVersion = latestVersionFor(aggregate)
        val unpublishedEvents = aggregate.unpublishedEvents().map {
            val eventType = when (it) {
                is MerchantNameChanged -> "MerchantNameChanged"
                is MerchantOnboarded -> "MerchantOnboarded"
            }
            EventEnvelope.wrap(
                id = EventEnvelope.Id.of(UUID.randomUUID()),
                streamId = EventEnvelope.StreamId.of("merchant-${aggregate.aggregateId}"),
                payload = it,
                metadata = Metadata.of(eventType),
                version = ++latestVersion,
                createdAt = LocalDateTime.now(),
            )
        }.map {
            EventStoreRecord().apply {
                id = it.id.value
                streamId = it.streamId.value
                payload = JSONB.jsonb(objectMapper.writeValueAsString(it.payload))
                metadata = JSONB.jsonb(objectMapper.writeValueAsString(it.metadata))
                version = it.version
                createdAt = it.createdAt
            }
        }
        dslContext.insertInto(EVENT_STORE)
            .set(unpublishedEvents)
            .execute()
    }

    override fun findBy(aggregateId: Id): Merchant? = eventsFor(aggregateId)
        .fold(null) { merchant: Merchant?, storedEvent ->
            when (storedEvent) {
                is MerchantOnboarded -> Merchant.on(storedEvent)
                is MerchantNameChanged -> merchant!!.on(storedEvent)
            }
        }

    override fun getBy(aggregateId: Id): Merchant = findBy(aggregateId)
        ?: throw AggregateNotFound("Merchant $aggregateId not found")

    private fun latestVersionFor(aggregate: Merchant): Int = dslContext.selectFrom(EVENT_STORE)
        .where(EVENT_STORE.STREAM_ID.equal("merchant-${aggregate.aggregateId}"))
        .orderBy(EVENT_STORE.VERSION)
        .map { it.version!! }
        .ifEmpty { listOf(0) }
        .max()

    private fun eventsFor(aggregateId: Id): List<MerchantEvent> = dslContext.selectFrom(EVENT_STORE)
        .where(EVENT_STORE.STREAM_ID.equal("merchant-$aggregateId"))
        .orderBy(EVENT_STORE.VERSION)
        .map {
            val metadata = objectMapper.readValue<EventEnvelope.Metadata>(it.metadata!!.data())
            when (metadata.eventType) {
                "MerchantNameChanged" -> objectMapper.readValue<MerchantNameChanged>(it.payload!!.data())
                "MerchantOnboarded" -> objectMapper.readValue<MerchantOnboarded>(it.payload!!.data())
                else -> error("Unknown event type ${metadata.eventType}")
            }
        }
}

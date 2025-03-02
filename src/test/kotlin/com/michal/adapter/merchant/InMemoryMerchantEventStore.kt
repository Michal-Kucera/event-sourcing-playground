@file:Suppress("RemoveRedundantQualifierName")

package com.michal.adapter.merchant

import com.michal.application.domain.merchant.Merchant
import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.MerchantEvent
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.application.domain.merchant.MerchantEventStore
import com.michal.application.domain.sharedkernel.eventsourcing.EventStore.AggregateNotFound

class InMemoryMerchantEventStore : MerchantEventStore {
    private val storedEvents = mutableMapOf<Merchant.Id, List<MerchantEvent>>()

    override fun storeEventsFor(aggregate: Merchant) {
        storedEvents[aggregate.aggregateId] = eventsFor(aggregate.aggregateId) + aggregate.unpublishedEvents()
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

    fun eventsFor(aggregateId: Id): List<MerchantEvent> = storedEvents[aggregateId] ?: emptyList()
}

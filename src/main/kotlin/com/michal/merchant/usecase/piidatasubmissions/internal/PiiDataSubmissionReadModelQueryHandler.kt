package com.michal.merchant.usecase.piidatasubmissions.internal

import com.michal.merchant.domain.event.MerchantEvent
import com.michal.merchant.usecase.piidatasubmissions.PiiDataSubmissionReadModel
import com.michal.merchant.usecase.piidatasubmissions.PiiDataSubmissionReadModelQuery
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class PiiDataSubmissionReadModelQueryHandler(
    private val eventStore: EventStore
) {

    @QueryHandler
    fun handleQuery(query: PiiDataSubmissionReadModelQuery): Optional<PiiDataSubmissionReadModel> = eventStore
        .readEvents(query.merchantId.toString())
        .asSequence()
        .map { it.payload }
        .filterIsInstance<MerchantEvent.PiiDataSubmitted>()
        .firstOrNull { it.version == query.version }
        ?.let { PiiDataSubmissionReadModel.apply(it) }
        .let { Optional.ofNullable(it) }
}

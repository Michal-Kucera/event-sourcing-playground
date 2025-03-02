package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.sharedkernel.eventsourcing.EventStore

interface MerchantEventStore : EventStore<Merchant, Id>

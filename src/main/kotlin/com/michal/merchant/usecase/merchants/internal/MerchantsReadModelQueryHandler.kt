package com.michal.merchant.usecase.merchants.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.michal.jooq.public.tables.references.MERCHANT_PROJECTION
import com.michal.merchant.usecase.merchants.internal.MerchantReadModelProjector.Projection
import com.michal.merchant.usecase.merchants.MerchantsReadModel
import com.michal.merchant.usecase.merchants.MerchantsReadModelQuery
import org.axonframework.queryhandling.QueryHandler
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component
class MerchantsReadModelQueryHandler(
    private val jooqContext: DSLContext,
    private val objectMapper: ObjectMapper,
) {

    @QueryHandler
    fun handle(query: MerchantsReadModelQuery): List<MerchantsReadModel> = jooqContext.selectFrom(MERCHANT_PROJECTION)
        .toList()
        .map { objectMapper.readValue(it.data!!.data(), Projection::class.java) }
        .map { MerchantsReadModel.from(it) }
}

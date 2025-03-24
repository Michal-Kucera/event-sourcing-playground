package com.michal.merchant.merchants.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.michal.jooq.public.tables.references.MERCHANT_PROJECTION
import com.michal.merchant.merchants.MerchantByIdReadModel
import com.michal.merchant.merchants.MerchantByIdReadModelQuery
import com.michal.merchant.merchants.internal.MerchantReadModelProjector.Projection
import org.axonframework.queryhandling.QueryHandler
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class MerchantByIdReadModelQueryHandler(
    private val jooqContext: DSLContext,
    private val objectMapper: ObjectMapper,
) {

    @QueryHandler
    fun handle(
        query: MerchantByIdReadModelQuery
    ): Optional<MerchantByIdReadModel> = jooqContext.selectFrom(MERCHANT_PROJECTION)
        .where(MERCHANT_PROJECTION.MERCHANT_ID.eq(query.merchantId.value))
        .singleOrNull()
        ?.let {
            MerchantByIdReadModel.from(
                projection = objectMapper.readValue(it.data!!.data(), Projection::class.java),
                latestSequenceNumber = requireNotNull(it.latestSequenceNumber)
            )
        }
        .let { Optional.ofNullable(it) }
}

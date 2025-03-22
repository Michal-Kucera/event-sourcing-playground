package com.michal.merchant.usecase.merchants

import com.fasterxml.jackson.databind.ObjectMapper
import com.michal.jooq.public.tables.references.MERCHANT_PROJECTION
import com.michal.merchant.usecase.merchants.MerchantReadModelProjector.Projection
import org.jooq.DSLContext
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class MerchantsResource(
    private val jooqContext: DSLContext,
    private val objectMapper: ObjectMapper,
) {

    @GetMapping("/merchants")
    @ResponseStatus(OK)
    fun findAllMerchants() = jooqContext.selectFrom(MERCHANT_PROJECTION)
        .toList()
        .map { objectMapper.readValue(it.data!!.data(), Projection::class.java) }

    @GetMapping("/merchants/{merchant-id}")
    fun findMerchantById(@PathVariable("merchant-id") merchantId: UUID) = jooqContext.selectFrom(MERCHANT_PROJECTION)
        .where(MERCHANT_PROJECTION.MERCHANT_ID.eq(merchantId))
        .singleOrNull()
        ?.let {
            val projection = objectMapper.readValue(it.data!!.data(), Projection::class.java)
            ok().headers { headers ->
                headers.set("X-LATEST-SEQUENCE-NUMBER", it.latestSequenceNumber.toString())
            }.body(projection)
        }
        ?: notFound().build()
}

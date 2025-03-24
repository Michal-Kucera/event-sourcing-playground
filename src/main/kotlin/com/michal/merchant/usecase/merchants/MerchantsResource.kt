package com.michal.merchant.usecase.merchants

import com.michal.merchant.domain.valueobject.MerchantId
import org.axonframework.extensions.kotlin.queryMany
import org.axonframework.extensions.kotlin.queryOptional
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class MerchantsResource(
    private val queryGateway: QueryGateway,
) {

    @GetMapping("/merchants")
    fun findAllMerchants() = queryGateway
        .queryMany<MerchantsReadModel, MerchantsReadModelQuery>(MerchantsReadModelQuery)
        .thenApply { ok(it) }
        .get()

    @GetMapping("/merchants/{merchant-id}")
    fun findMerchantById(
        @PathVariable("merchant-id") merchantId: UUID
    ) = queryGateway.queryOptional<MerchantByIdReadModel, MerchantByIdReadModelQuery>(
        MerchantByIdReadModelQuery(MerchantId.of(merchantId)),
    ).thenApply {
        when {
            it.isPresent -> ok().headers { headers ->
                headers.set("X-LATEST-SEQUENCE-NUMBER", it.get().latestSequenceNumber.toString())
            }.body(it.get())

            else -> notFound().build()
        }
    }.get()
}

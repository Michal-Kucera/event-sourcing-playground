package com.michal.merchant.piidatasubmissions

import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.valueobject.PiiData.Version
import com.michal.merchant.piidatasubmissions.PiiDataSubmissionReadModelQueryHandler.PiiDataSubmissionReadModel
import com.michal.merchant.piidatasubmissions.PiiDataSubmissionReadModelQueryHandler.PiiDataSubmissionReadModelQuery
import org.axonframework.extensions.kotlin.queryOptional
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class PiiDataSubmissionsResource(
    private val queryGateway: QueryGateway,
) {

    @GetMapping("/merchants/{merchant-id}/pii-data/{version}")
    fun findSpecificPiiDataVersionForMerchant(
        @PathVariable("merchant-id") merchantId: UUID,
        @PathVariable("version") version: Int
    ) = queryGateway.queryOptional<PiiDataSubmissionReadModel, PiiDataSubmissionReadModelQuery>(
        PiiDataSubmissionReadModelQuery(MerchantId.of(merchantId), Version.of(version)),
    ).thenApply {
        when {
            it.isPresent -> ok(it.get())
            else -> notFound().build()
        }
    }.get()
}

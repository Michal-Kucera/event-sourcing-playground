package com.michal.submitpiidata

import com.michal.domain.merchant.Merchant
import com.michal.domain.merchant.Merchant.PiiData
import com.michal.domain.merchant.MerchantCommand.SubmitPiiData
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.concurrent.CompletableFuture

@RestController
class SubmitPiiDataResource(
    private val commandGateway: CommandGateway,
) {

    @PostMapping("/merchants/{merchant-id}/pii-data")
    @ResponseStatus(NO_CONTENT)
    fun submitPiiData(
        @PathVariable("merchant-id") merchantId: UUID,
        @RequestBody payload: Payload,
    ): CompletableFuture<SubmitPiiData> = commandGateway.send(
        SubmitPiiData(
            aggregateId = Merchant.Id.of(merchantId),
            name = PiiData.Name.of(payload.name),
            legalEntityIdentifiers = PiiData.LegalEntityIdentifiers.of(payload.vatNumber, payload.registrationNumber),
        ),
    )

    data class Payload(
        val name: String,
        val vatNumber: String?,
        val registrationNumber: String?
    )
}

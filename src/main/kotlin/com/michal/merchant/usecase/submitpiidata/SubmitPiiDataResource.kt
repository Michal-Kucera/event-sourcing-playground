package com.michal.merchant.usecase.submitpiidata

import com.michal.sharedkernel.valueobject.Country
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.command.MerchantCommand.SubmitPiiData
import com.michal.merchant.domain.valueobject.PiiData
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
            aggregateId = MerchantId.of(merchantId),
            name = PiiData.Name.of(payload.name),
            legalEntityId = PiiData.LegalEntityId.of(
                country = Country.from(payload.countryCode),
                vatNumber = payload.vatNumber,
                registrationNumber = payload.registrationNumber
            ),
            legalAddress = PiiData.LegalAddress.of(
                country = Country.from(payload.countryCode),
                postCode = payload.postCode,
                city = payload.city,
                addressLine1 = payload.addressLine1,
                addressLine2 = payload.addressLine2,
            ),
        ),
    )

    data class Payload(
        val name: String,
        val vatNumber: String?,
        val registrationNumber: String?,
        val countryCode: String,
        val postCode: String,
        val city: String,
        val addressLine1: String,
        val addressLine2: String?,
    )
}

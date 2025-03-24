package com.michal.merchant.reconcilepiidata

import com.michal.merchant.domain.command.MerchantCommand.ReconcilePiiData
import com.michal.merchant.domain.command.MerchantCommand.SubmitPiiData
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.valueobject.PiiData
import com.michal.sharedkernel.valueobject.Country
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
class ReconcilePiiDataResource(
    private val commandGateway: CommandGateway,
) {

    @PostMapping("/merchants/{merchant-id}/pii-data/reconcile")
    @ResponseStatus(NO_CONTENT)
    fun submitPiiData(
        @PathVariable("merchant-id") merchantId: UUID,
        @RequestBody payload: Payload,
    ): CompletableFuture<SubmitPiiData> = commandGateway.send(
        ReconcilePiiData(
            aggregateId = MerchantId.of(merchantId),
            olderVersion = PiiData.Version.of(payload.olderVersion),
            newerVersion = PiiData.Version.of(payload.newerVersion),
            reconciledName = PiiData.Name.of(payload.reconciledName),
            reconciledLegalEntityId = PiiData.LegalEntityId.of(
                country = Country.from(payload.reconciledCountryCode),
                vatNumber = payload.reconciledVatNumber,
                registrationNumber = payload.reconciledRegistrationNumber
            ),
            reconciledLegalAddress = PiiData.LegalAddress.of(
                country = Country.from(payload.reconciledCountryCode),
                postCode = payload.reconciledPostCode,
                city = payload.reconciledCity,
                addressLine1 = payload.reconciledAddressLine1,
                addressLine2 = payload.reconciledAddressLine2,
            ),
        ),
    )

    data class Payload(
        val olderVersion: Int,
        val newerVersion: Int,
        val reconciledName: String,
        val reconciledVatNumber: String?,
        val reconciledRegistrationNumber: String?,
        val reconciledCountryCode: String,
        val reconciledPostCode: String,
        val reconciledCity: String,
        val reconciledAddressLine1: String,
        val reconciledAddressLine2: String?,
    )
}

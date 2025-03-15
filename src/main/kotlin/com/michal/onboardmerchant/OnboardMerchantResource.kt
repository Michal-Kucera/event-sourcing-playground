package com.michal.onboardmerchant

import com.michal.domain.merchant.Merchant
import com.michal.domain.merchant.MerchantCommand.OnboardMerchant
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.concurrent.CompletableFuture

@RestController
class OnboardMerchantResource(
    private val commandGateway: CommandGateway,
) {

    @PostMapping("/merchants")
    @ResponseStatus(CREATED)
    fun onboardMerchant(
        @RequestBody payload: Payload
    ): CompletableFuture<OnboardMerchant> = commandGateway.send(
        OnboardMerchant(
            aggregateId = Merchant.Id.of(payload.merchantId),
            country = Merchant.Country.from(payload.countryCode),
            currency = Merchant.Currency.from(payload.currencyCode),
        ),
    )

    data class Payload(
        val merchantId: UUID,
        val countryCode: String,
        val currencyCode: String
    )
}

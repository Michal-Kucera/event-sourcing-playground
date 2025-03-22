package com.michal.merchant.usecase.onboardmerchant

import com.michal.merchant.domain.valueobject.AnonymizedData
import com.michal.merchant.domain.valueobject.AnonymizedData.KitchenType
import com.michal.sharedkernel.valueobject.Country
import com.michal.sharedkernel.valueobject.Currency
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.command.MerchantCommand.OnboardMerchant
import com.michal.sharedkernel.valueobject.PlatformId
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
            aggregateId = MerchantId.of(payload.merchantId),
            platformId = PlatformId.of(payload.platformId, payload.merchantExternalId),
            legalAddress = AnonymizedData.LegalAddress.of(
                country = Country.from(payload.countryCode),
                postCode = payload.postCode,
                city = payload.city,
                addressLine1 = payload.addressLine1,
                addressLine2 = payload.addressLine2,
            ),
            currency = Currency.from(payload.currencyCode),
            kitchenTypes = payload.kitchenTypes.map { KitchenType.of(it) }.toSet(),
        ),
    )

    data class Payload(
        val merchantId: UUID,
        val platformId: UUID,
        val merchantExternalId: String,
        val countryCode: String,
        val postCode: String?,
        val city: String?,
        val addressLine1: String?,
        val addressLine2: String?,
        val currencyCode: String,
        val kitchenTypes: Set<String>
    )
}

package com.michal.application.usecase.merchant

import com.michal.application.domain.merchant.Merchant
import com.michal.application.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.application.domain.merchant.MerchantEventStore
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

class OnboardMerchantUseCase(
    private val eventStore: MerchantEventStore,
) {
    operator fun invoke(command: Command): Unit = with(command) {
        val merchant = Merchant.handle(
            OnboardMerchant(
                merchantId,
                platformId,
                foundingDate,
                platformPartnershipStartedDate,
                businessTypes,
                preferredCurrency,
                paymentAccountNumberEnding,
                legalRepresentativePhoneEnding,
                countryCode,
                postCode,
                city,
                addressLine1,
                addressLine2,
            )
        )
        eventStore.storeEventsFor(merchant)
    }

    data class Command(
        val merchantId: Merchant.Id,
        val platformId: UUID,
        val foundingDate: LocalDate?,
        val platformPartnershipStartedDate: LocalDate?,
        val businessTypes: List<String>, // more generic than kichen types
        val preferredCurrency: Currency,
        val paymentAccountNumberEnding: String,
        val legalRepresentativePhoneEnding: String,
        val countryCode: String,
        val postCode: String?,
        val city: String?,
        val addressLine1: String?,
        val addressLine2: String?,
    )
}

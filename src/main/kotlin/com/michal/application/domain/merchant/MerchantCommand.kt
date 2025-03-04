package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.sharedkernel.eventsourcing.Command
import java.time.LocalDate
import java.util.Currency
import java.util.UUID

sealed interface MerchantCommand : Command {

    data class OnboardMerchant(
        val id: Id,
        val platformId: UUID,
        val foundingDate: LocalDate?,
        val platformPartnershipStartedDate: LocalDate?,
        val businessTypes: List<String>,
        val preferredCurrency: Currency,
        val paymentAccountNumberEnding: String,
        val legalRepresentativePhoneEnding: String,
        val countryCode: String,
        val postCode: String?,
        val city: String?,
        val addressLine1: String?,
        val addressLine2: String?,
    ) : MerchantCommand

    data class ChangeMerchantName(
        val newName: Name,
    ) : MerchantCommand
}

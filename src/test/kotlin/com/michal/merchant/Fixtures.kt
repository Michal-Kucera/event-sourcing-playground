@file:Suppress("RemoveRedundantQualifierName")

package com.michal.merchant

import com.michal.merchant.domain.command.MerchantCommand.OnboardMerchant
import com.michal.merchant.domain.command.MerchantCommand.SubmitPiiData
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import com.michal.merchant.domain.valueobject.AnonymizedData
import com.michal.merchant.domain.valueobject.AnonymizedData.KitchenType
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.valueobject.PiiData
import com.michal.merchant.domain.valueobject.PiiData.Version
import com.michal.sharedkernel.validStable
import com.michal.sharedkernel.valueobject.Country
import com.michal.sharedkernel.valueobject.Country.Companion.UNITED_STATES_OF_AMERICA
import com.michal.sharedkernel.valueobject.Currency.Companion.USD
import com.michal.sharedkernel.valueobject.PlatformId

fun MerchantId.Companion.validStable() = MerchantId.of("9cbf676b-552b-460d-8da4-029e97ca95b7")

fun OnboardMerchant.Companion.validStable() = OnboardMerchant(
    MerchantId.validStable(),
    PlatformId.validStable(),
    AnonymizedData.LegalAddress.validStable(),
    USD,
    KitchenType.validStableSet()
)

fun MerchantOnboarded.Companion.validStable(
    country: Country = UNITED_STATES_OF_AMERICA,
) = MerchantOnboarded(
    MerchantId.validStable(),
    PlatformId.validStable(),
    AnonymizedData.LegalAddress.validStable(country),
    USD,
    KitchenType.validStableSet()
)

fun AnonymizedData.LegalAddress.Companion.validStable(
    country: Country = UNITED_STATES_OF_AMERICA,
) = AnonymizedData.LegalAddress.of(
    country = country,
    postCode = "08030",
    city = "Washington DC",
    addressLine1 = "Trumpstreet",
    addressLine2 = null
)

fun KitchenType.Companion.validStableSet() = setOf(KitchenType.of("Korean"), KitchenType.of("Asian"))

fun SubmitPiiData.Companion.validStable(
    country: Country = UNITED_STATES_OF_AMERICA,
) = SubmitPiiData(
    MerchantId.validStable(),
    PiiData.Name.validStable(),
    PiiData.LegalEntityId.validStable(),
    PiiData.LegalAddress.validStable(country)
)

fun PiiDataSubmitted.Companion.validStable() = PiiDataSubmitted(
    MerchantId.validStable(),
    Version.initial(),
    PiiData.Name.validStable(),
    PiiData.LegalEntityId.validStable(),
    PiiData.LegalAddress.validStable()
)

fun PiiData.Name.Companion.validStable() = PiiData.Name.of("Paulo Merido")

fun PiiData.LegalEntityId.Companion.validStable() = PiiData.LegalEntityId.of(
    country = UNITED_STATES_OF_AMERICA,
    vatNumber = "123456789",
    registrationNumber = "987654321"
)

fun PiiData.LegalAddress.Companion.validStable(
    country: Country = UNITED_STATES_OF_AMERICA,
) = PiiData.LegalAddress.of(
    country = country,
    postCode = "08030",
    city = "Washington DC",
    addressLine1 = "Trumpstreet 4",
    addressLine2 = "Block 1"
)

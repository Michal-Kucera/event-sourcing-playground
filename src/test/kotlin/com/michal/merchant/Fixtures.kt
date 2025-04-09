@file:Suppress("RemoveRedundantQualifierName")

package com.michal.merchant

import com.michal.merchant.domain.command.MerchantCommand.OnboardMerchant
import com.michal.merchant.domain.command.MerchantCommand.ReconcilePiiData
import com.michal.merchant.domain.command.MerchantCommand.SendWelcomeEmail
import com.michal.merchant.domain.command.MerchantCommand.SubmitPiiData
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataReconciled
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import com.michal.merchant.domain.event.MerchantEvent.WelcomeEmailSent
import com.michal.merchant.domain.valueobject.AnonymizedData
import com.michal.merchant.domain.valueobject.AnonymizedData.KitchenType
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.valueobject.PiiData
import com.michal.merchant.domain.valueobject.PiiData.Version
import com.michal.sharedkernel.validStable
import com.michal.sharedkernel.valueobject.Country
import com.michal.sharedkernel.valueobject.Country.Companion.UNITED_STATES_OF_AMERICA
import com.michal.sharedkernel.valueobject.Currency.Companion.USD
import com.michal.sharedkernel.valueobject.Email
import com.michal.sharedkernel.valueobject.PlatformId

fun MerchantId.Companion.validStable() = MerchantId.of("9cbf676b-552b-460d-8da4-029e97ca95b7")

fun OnboardMerchant.Companion.validStable() = OnboardMerchant(
    MerchantId.validStable(),
    PlatformId.validStable(),
    AnonymizedData.LegalAddress.validStable(),
    USD,
    KitchenType.validStable()
)

fun MerchantOnboarded.Companion.validStable(
    country: Country = UNITED_STATES_OF_AMERICA,
) = MerchantOnboarded(
    MerchantId.validStable(),
    PlatformId.validStable(),
    AnonymizedData.LegalAddress.validStable(country),
    USD,
    KitchenType.validStable()
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

fun KitchenType.Companion.validStable() = setOf(KitchenType.of("Korean"), KitchenType.of("Asian"))

fun SendWelcomeEmail.Companion.validStable() = SendWelcomeEmail(
    MerchantId.validStable(),
    contentTemplate = "Welcome onboard, {{name}}!"
)

fun WelcomeEmailSent.Companion.validStable() = WelcomeEmailSent(
    MerchantId.validStable(),
    to = Email.validStable(),
    content = "Welcome onboard, ${PiiData.Name.validStable()}!"
)

fun SubmitPiiData.Companion.validStable(
    country: Country = UNITED_STATES_OF_AMERICA,
) = SubmitPiiData(
    MerchantId.validStable(),
    PiiData.Name.validStable(),
    Email.validStable(),
    PiiData.LegalEntityId.validStable(country),
    PiiData.LegalAddress.validStable(country)
)

fun PiiDataSubmitted.Companion.validStable() = PiiDataSubmitted(
    MerchantId.validStable(),
    Version.initial(),
    PiiData.Name.validStable(),
    Email.validStable(),
    PiiData.LegalEntityId.validStable(),
    PiiData.LegalAddress.validStable()
)

fun SubmitPiiData.Companion.validStableV2(
) = SubmitPiiData(
    MerchantId.validStable(),
    PiiData.Name.of("Jennifer Shinde"),
    Email.of("jennifer.shinde@hello.com"),
    PiiData.LegalEntityId.of(
        country = UNITED_STATES_OF_AMERICA,
        vatNumber = "45678901",
        registrationNumber = "76540987"
    ),
    PiiData.LegalAddress.of(
        country = UNITED_STATES_OF_AMERICA,
        postCode = "08031",
        city = "Chicago",
        addressLine1 = "Cali 123",
        addressLine2 = null
    )
)

fun PiiDataSubmitted.Companion.validStableV2() = PiiDataSubmitted(
    MerchantId.validStable(),
    Version.of(2),
    PiiData.Name.of("Jennifer Shinde"),
    Email.of("jennifer.shinde@hello.com"),
    PiiData.LegalEntityId.of(
        country = UNITED_STATES_OF_AMERICA,
        vatNumber = "45678901",
        registrationNumber = "76540987"
    ),
    PiiData.LegalAddress.of(
        country = UNITED_STATES_OF_AMERICA,
        postCode = "08031",
        city = "Chicago",
        addressLine1 = "Cali 123",
        addressLine2 = null
    )
)

fun PiiDataSubmitted.Companion.validStableV3() = PiiDataSubmitted(
    MerchantId.validStable(),
    Version.of(3),
    PiiData.Name.of("Mona Chauhan"),
    Email.of("mona.chauhan@hello.com"),
    PiiData.LegalEntityId.of(
        country = UNITED_STATES_OF_AMERICA,
        vatNumber = "98754567",
        registrationNumber = "345678987"
    ),
    PiiData.LegalAddress.of(
        country = UNITED_STATES_OF_AMERICA,
        postCode = "08032",
        city = "San Francisco",
        addressLine1 = "Sesame street 33",
        addressLine2 = "2nd floor"
    )
)

fun PiiData.Name.Companion.validStable() = PiiData.Name.of("Paulo Merido")

fun PiiData.LegalEntityId.Companion.validStable(
    country: Country = UNITED_STATES_OF_AMERICA
) = PiiData.LegalEntityId.of(
    country = country,
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

fun ReconcilePiiData.Companion.validStable() = ReconcilePiiData(
    MerchantId.validStable(),
    PiiDataSubmitted.validStable().version,
    PiiDataSubmitted.validStableV2().version,
    PiiDataSubmitted.validStableV2().name,
    PiiDataSubmitted.validStableV2().legalEntityId,
    PiiDataSubmitted.validStableV2().legalAddress
)

fun ReconcilePiiData.Companion.validStableV2() = ReconcilePiiData(
    MerchantId.validStable(),
    PiiDataSubmitted.validStableV2().version,
    PiiDataSubmitted.validStableV3().version,
    PiiDataSubmitted.validStableV3().name,
    PiiDataSubmitted.validStableV3().legalEntityId,
    PiiDataSubmitted.validStableV3().legalAddress
)

fun PiiDataReconciled.Companion.validStable() = PiiDataReconciled(
    MerchantId.validStable(),
    PiiDataSubmitted.validStable().version,
    PiiDataSubmitted.validStableV2().version,
    PiiDataSubmitted.validStableV2().name,
    PiiDataSubmitted.validStableV2().legalEntityId,
    PiiDataSubmitted.validStableV2().legalAddress
)

fun PiiDataReconciled.Companion.validStableV2() = PiiDataReconciled(
    MerchantId.validStable(),
    PiiDataSubmitted.validStableV2().version,
    PiiDataSubmitted.validStableV3().version,
    PiiDataSubmitted.validStableV2().name,
    PiiDataSubmitted.validStableV2().legalEntityId,
    PiiDataSubmitted.validStableV3().legalAddress
)

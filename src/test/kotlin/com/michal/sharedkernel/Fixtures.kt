@file:Suppress("RemoveRedundantQualifierName")

package com.michal.sharedkernel

import com.michal.merchant.sendwelcomeemail.EmailSender
import com.michal.sharedkernel.valueobject.Email
import com.michal.sharedkernel.valueobject.PlatformId
import io.kotest.matchers.maps.shouldContain

fun PlatformId.Companion.validStable() = PlatformId.of(
    platformId = "e6ceecdb-5ad0-454d-a428-f9f0f873f69b",
    merchantExternalId = "026c516959354797bd1a7bdc03e2e8c4"
)

fun Email.Companion.validStable() = Email.of("paulo.merido@hello.com")

class FakeEmailSender : EmailSender {
    private val sentEmails = mutableMapOf<Email, String>()

    override fun sendEmail(to: Email, content: String) {
        sentEmails += to to content
    }

    fun verifyEmailWasSent(to: Email, content: String) {
        sentEmails shouldContain (to to content)
    }
}

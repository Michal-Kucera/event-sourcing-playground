package com.michal.merchant.sendwelcomeemail

import com.michal.sharedkernel.valueobject.Email
import org.springframework.stereotype.Component

@Component
class SendgridEmailSender : EmailSender {
    override fun sendEmail(to: Email, content: String) {
        println("Sending email to $to, content: $content")
    }
}
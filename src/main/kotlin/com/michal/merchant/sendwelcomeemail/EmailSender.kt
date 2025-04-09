package com.michal.merchant.sendwelcomeemail

import com.michal.sharedkernel.valueobject.Email

interface EmailSender {
    fun sendEmail(to: Email, content: String)
}

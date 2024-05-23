package com.poulastaa.utils

object Constants {
    private const val SECURITY_TYPE_SESSION = "session-auth"

    const val SMS_EMAIL_GOOGLE_SMTP_HOST = "smtp.gmail.com"
    const val SMS_EMAIL_PORT = "587"

    const val SESSION_NAME_GOOGLE = "GOOGLE_USER_SESSION"

    const val DEFAULT_SESSION_MAX_AGE = 7L * 24 * 3600 // 7 days

    const val SIGNUP_VERIFICATION_MAIL_TOKEN_CLAIM_KEY = "signUpEmailVerify"
    const val LOGIN_VERIFICATION_MAIL_TOKEN_CLAIM_KEY = "logInEmailVerify"
    const val VERIFICATION_MAIL_TOKEN_TIME = 240000L // 4 minute
}
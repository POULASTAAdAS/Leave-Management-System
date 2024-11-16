package com.poulastaa.utils

object Constants {
    const val PROFILE_FOLDER_PATH = "./profile/"
    const val MEDICAL_FOLDER_PATH = "./medicalFolder/"
    const val SMS_EMAIL_GOOGLE_SMTP_HOST = "smtp.gmail.com"
    const val SMS_EMAIL_PORT = "587"

    const val DEFAULT_SESSION_MAX_AGE = 7L * 24 * 3600 // 7 days

    const val SIGNUP_VERIFICATION_MAIL_TOKEN_CLAIM_KEY = "signUpEmailVerify"
    const val LOGIN_VERIFICATION_MAIL_TOKEN_CLAIM_KEY = "logInEmailVerify"
    const val VERIFICATION_MAIL_TOKEN_TIME = 240000L // 4 minute

    const val SESSION_AUTH = "session-auth"

    const val BASE_URL = "http://lms.poulastaa.online:8080"
}
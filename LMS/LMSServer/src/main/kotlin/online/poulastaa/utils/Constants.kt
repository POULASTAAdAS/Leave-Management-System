package online.poulastaa.utils

object Constants {
    const val BASE_URL = ""

    const val SESSION_NAME = "GOOGLE_USER_SESSION"

    const val SMS_EMAIL_GOOGLE_SMTP_HOST = "smtp.gmail.com"
    const val SMS_EMAIL_PORT = "587"

    private const val SECURITY_TYPE_EMAIL = "email-auth"

    val SECURITY_LIST = arrayOf(SECURITY_TYPE_EMAIL)

    const val DEFAULT_SESSION_MAX_AGE = 7L * 24 * 3600 // 7 days
}
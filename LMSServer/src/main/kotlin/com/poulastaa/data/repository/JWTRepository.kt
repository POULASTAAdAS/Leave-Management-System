package com.poulastaa.data.repository

import com.poulastaa.utils.Constants.VERIFICATION_MAIL_TOKEN_CLAIM_KEY
import com.poulastaa.utils.Constants.VERIFICATION_MAIL_TOKEN_TIME

interface JWTRepository {
    fun generateVerificationMailToken(
        sub: String = "VerificationMail",
        email: String,
        claimName: String = VERIFICATION_MAIL_TOKEN_CLAIM_KEY,
        validationTime: Long = VERIFICATION_MAIL_TOKEN_TIME
    ): String

    fun verifyJWTToken(token: String, claim: String): String?
}
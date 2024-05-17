package com.poulastaa.data.model

sealed class EndPoints(val route: String) {
    data object Auth : EndPoints(route = "/api/auth")

    data object VerifyEmail : EndPoints(route = "/api/auth/verifyEmail")
    data object EmailVerificationCheck : EndPoints(route = "/api/auth/emailVerificationCheck")

    data object SetDetails : EndPoints(route = "/api/auth/setDetails")

    data object UnAuthorised : EndPoints(route = "/api/unauthorised")
}
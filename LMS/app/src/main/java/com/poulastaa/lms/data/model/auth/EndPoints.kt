package com.poulastaa.lms.data.model.auth

sealed class EndPoints(val route: String) {

    data object Auth : EndPoints(route = "/api/auth")

    data object VerifySignUpEmail : EndPoints(route = "/api/auth/verifySignUpEmail")
    data object SignUpEmailVerificationCheck :
        EndPoints(route = "/api/auth/signUpEmailVerificationCheck")

    data object VerifyLogInEmail : EndPoints(route = "/api/auth/verifyLogInEmail")
    data object LogInEmailVerificationCheck :
        EndPoints(route = "/api/auth/logInEmailVerificationCheck")

    data object SetDetails : EndPoints(route = "/api/auth/setDetails")

    data object GetDetails : EndPoints(route = "/api/auth/getDetails")

    data object UpdateProfilePic : EndPoints(route = "/api/updateProfilePic")

    data object UpdateDetails : EndPoints(route = "/api/auth/updateDetails")
}
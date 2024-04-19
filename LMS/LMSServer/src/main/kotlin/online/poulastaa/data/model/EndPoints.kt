package online.poulastaa.data.model

sealed class EndPoints(val route: String) {
    data object Auth : EndPoints(route = "/api/auth")

    data object UnAuthorised : EndPoints(route = "/api/unauthorised")
}
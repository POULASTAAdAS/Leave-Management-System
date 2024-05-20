package com.poulastaa.lms.navigation

sealed class Screens(val route: String) {
    data object Auth : Screens("/auth")

    data object StoreDetails : Screens("/storeDetails")

    data object Home : Screens("/home")
}
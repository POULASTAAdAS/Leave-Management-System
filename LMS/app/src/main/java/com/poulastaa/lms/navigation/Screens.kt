package com.poulastaa.lms.navigation

sealed class Screens(val route: String) {
    data object Auth : Screens("/auth")

    data object StoreDetails : Screens("/storeDetails")

    data object Home : Screens("/home")

    data object Profile : Screens("/profile")

    data object ApplyLeave : Screens("/applyLeave")
    data object LeaveHistory : Screens("/leaveHistory")
    data object LeaveStatus : Screens("/leaveStatus")

    // hod
    data object ApproveLeave : Screens("/approveLeave")
    data object ViewLeave : Screens("/viewLeave")
    data object ViewReport : Screens("/viewReport")

    // principle
    data object DefineDepartmentInCharge : Screens("/defineDepartmentInCharge")
    data object Add : Screens("/add")

    data object EditDetails : Screens("/profile/editDetails") {
        enum class Args(val title: String) {
            NAME("name"),
            EMAIL("email"),
            PHONE_ONE("phoneOne"),
            PHONE_TWO("phoneTwo"),
            QUALIFICATION("qualification")
        }

        val PARAMS: String =
            "{${Args.NAME.title}}/{${Args.EMAIL.title}}/{${Args.PHONE_ONE.title}}/{${Args.PHONE_TWO.title}}/{${Args.QUALIFICATION.title}}"
    }
}
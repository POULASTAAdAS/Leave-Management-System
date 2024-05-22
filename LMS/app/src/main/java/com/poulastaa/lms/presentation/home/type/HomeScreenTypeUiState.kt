package com.poulastaa.lms.presentation.home.type

import com.poulastaa.lms.data.model.auth.LocalUser
import com.poulastaa.lms.data.model.home.UserType

data class HomeScreenTypeUiState(
    val userType: UserType = UserType.NON,
    val time: String = "Hello",
    val user: LocalUser = LocalUser()
)

package com.poulastaa.lms.presentation.home.type

import com.poulastaa.lms.data.model.auth.LocalUser

data class HomeScreenTypeUiState(
    val time: String = "Hello",
    val user: LocalUser = LocalUser(),
    val cookie: String = ""
)

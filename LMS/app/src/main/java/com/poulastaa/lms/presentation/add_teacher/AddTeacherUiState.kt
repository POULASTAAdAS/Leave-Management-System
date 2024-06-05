package com.poulastaa.lms.presentation.add_teacher

import com.poulastaa.lms.presentation.store_details.Holder

data class AddTeacherUiState(
    val isMakingApiCall: Boolean = false,
    val email: Holder = Holder(),
    val isValidEmail: Boolean = false,
)

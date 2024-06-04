package com.poulastaa.lms.presentation.profile.edit.details_edit.head

import com.poulastaa.lms.presentation.store_details.Holder

data class HeadDetailsEditUiState(
    val isMakingApiCall: Boolean = false,
    val name: Holder = Holder(),
    val email: Holder = Holder()
)

package com.poulastaa.lms.presentation.profile.edit.details_edit

import com.poulastaa.lms.presentation.store_details.Holder
import com.poulastaa.lms.presentation.store_details.ListHolder

data class DetailsEditUiState(
    val isInternet: Boolean = false,
    val isMakingApiCall: Boolean = false,

    val name: Holder = Holder(),
    val email: Holder = Holder(),
    val phoneOne: Holder = Holder(),
    val phoneTwo: Holder = Holder(),
    val qualification: ListHolder = ListHolder(
        all = listOf(
            "Dr.",
            "M.Com",
            "M.Tech",
            "MBA",
            "MCA",
            "MLib",
            "MSC",
            "Ph.D"
        )
    ),
)

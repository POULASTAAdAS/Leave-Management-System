package com.poulastaa.lms.presentation.store_details

import androidx.compose.runtime.Stable
import com.poulastaa.lms.ui.utils.UiText

@Stable
data class StoreDetailsUiState(
    val isInternet: Boolean = false,

    val userName: Holder = Holder(),
    val hrmsId: Holder = Holder(),
    val email: Holder = Holder(),
    val phoneOne: Holder = Holder(),
    val phoneTwo: Holder = Holder(),
    val bDay: DialogHolder = DialogHolder(),

    val gender: ListHolder = ListHolder(
        all = listOf("M", "F", "O")
    ),
    val designation: ListHolder = ListHolder(
        all = listOf(
            "Assistant Professor-I",
            "Assistant Professor-II",
            "Assistant Professor-III",
            "Associate Professor",
            "SCAT-I",
            "SCAT-II"
        )
    ),
    val department: ListHolder = ListHolder(
        all = listOf(
            "ASP(Advertisement and Sales Promotion)",
            "Bengali",
            "Botany",
            "Chemistry",
            "Commerce",
            "Computer Science",
            "Economics",
            "Education",
            "Electronic Science",
            "English",
            "Environmental Science",
            "Food & Nutrition",
            "Geography",
            "Hindi",
            "History",
            "Journalism & Mass Com.",
            "Mathematics",
            "Philosophy",
            "Physical Education",
            "Physics",
            "Physiology",
            "Sanskrit",
            "Sociology",
            "Urdu",
            "Zoology"
        )
    ),

    val joiningDate: DialogHolder = DialogHolder(),

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

    val experience: String = "",

    val presentAddress: HolderAddress = HolderAddress(),
    val homeAddress: HolderAddress = HolderAddress(),

    val isMakingApiCall: Boolean = false
)

data class Holder(
    val data: String = "",
    val isErr: Boolean = false,
    val errText: UiText = UiText.DynamicString("")
)

data class DialogHolder(
    val data: String = "",
    val isDialogOpen: Boolean = false,
    val isErr: Boolean = false,
    val errText: UiText = UiText.DynamicString("")
)

@Stable
data class ListHolder(
    val isDialogOpen: Boolean = false,
    val selected: String = "",
    val all: List<String> = emptyList(),
    val isErr: Boolean = false,
    val errText: UiText = UiText.DynamicString("")
)

data class HolderAddress(
    val houseNumber: Holder = Holder(),
    val street: Holder = Holder(),
    val city: Holder = Holder(
        data = "Kolkata"
    ),
    val zipCode: Holder = Holder(),
    val state: Holder = Holder(
        data = "West Bengal"
    ),
    val country: Holder = Holder(
        data = "India"
    )
)
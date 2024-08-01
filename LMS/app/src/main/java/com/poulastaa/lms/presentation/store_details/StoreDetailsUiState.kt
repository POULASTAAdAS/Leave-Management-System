package com.poulastaa.lms.presentation.store_details

import androidx.compose.runtime.Stable
import com.poulastaa.lms.ui.utils.UiText

@Stable
data class StoreDetailsUiState(
    val isInternet: Boolean = false,

    val prefix: ListHolder = ListHolder(
        all = listOf(
            "Dr.",
            "Mr.",
            "Ms."
        )
    ),

    val userName: Holder = Holder(),
    val hrmsId: Holder = Holder(),
    val email: Holder = Holder(),
    val phoneOne: Holder = Holder(),
    val phoneTwo: Holder = Holder(),
    val bDay: DialogHolder = DialogHolder(),

    val gender: ListHolder = ListHolder(
        all = listOf("Male", "Female", "Other")
    ),
    val designation: ListHolder = ListHolder(
        all = listOf(
            "Assistant Professor-I",
            "Assistant Professor-II",
            "Assistant Professor-III",
            "Associate Professor",
            "SACT-I",
            "SACT-II",
            "Clerk",
            "Elec. Cum Caretaker",
            "Gen./Pump Operator Cum Mechanic",
            "Guard",
            "Lab. Attendant",
            "Mali",
            "Peon",
            "Typist"
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
            "Zoology",
            "NTS"
        )
    ),

    val joiningDate: DialogHolder = DialogHolder(),

    val qualification: ListHolder = ListHolder(
        all = listOf(
            "Ph.D",
            "M.Tech",
            "M.Com",
            "MBA",
            "MCA",
            "MSC",
            "MLib",
            "Other"
        )
    ),

    val experience: String = "",

    val presentAddress: HolderAddress = HolderAddress(),
    val homeAddress: HolderAddress = HolderAddress(),

    val isMakingApiCall: Boolean = false,
)

data class Holder(
    val data: String = "",
    val isErr: Boolean = false,
    val errText: UiText = UiText.DynamicString(""),
)

data class DialogHolder(
    val data: String = "",
    val isDialogOpen: Boolean = false,
    val isErr: Boolean = false,
    val errText: UiText = UiText.DynamicString(""),
)

@Stable
data class ListHolder(
    val isDialogOpen: Boolean = false,
    val selected: String = "",
    val all: List<String> = emptyList(),
    val isErr: Boolean = false,
    val errText: UiText = UiText.DynamicString(""),
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
    ),
)
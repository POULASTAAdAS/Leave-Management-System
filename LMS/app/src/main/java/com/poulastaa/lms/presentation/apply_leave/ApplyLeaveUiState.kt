package com.poulastaa.lms.presentation.apply_leave

import android.net.Uri
import com.poulastaa.lms.data.model.auth.LocalUser
import com.poulastaa.lms.presentation.store_details.DialogHolder
import com.poulastaa.lms.presentation.store_details.Holder
import com.poulastaa.lms.presentation.store_details.ListHolder

data class ApplyLeaveUiState(
    val isInternet: Boolean = false,
    val isGettingLeaveBalance: Boolean = false,
    val isMakingApiCall: Boolean = false,
    val user: LocalUser = LocalUser(),

    val isSuccess: Boolean = false,

    val isDocNeeded: Boolean = false,
    val isDocErr: Boolean = false,
    val docUrl: Uri? = null,

    val balance: String = "0.0",

    val leaveType: ListHolder = ListHolder(),
    val dayType: ListHolder = ListHolder(
        all = listOf(
            "Full Day",
            "Half Day"
        ),
        selected = "Full Day"
    ),
    val fromDate: DialogHolder = DialogHolder(),
    val toDate: DialogHolder = DialogHolder(),
    val totalDays: String = "0",
    val leaveReason: Holder = Holder(),

    val addressDuringLeaveOutStation: Holder = Holder(),
    val addressDuringLeave: ListHolder = ListHolder(
        all = listOf(
            "Present",
            "Home",
            "OutStation"
        )
    ),
    val path: ListHolder = ListHolder()
)

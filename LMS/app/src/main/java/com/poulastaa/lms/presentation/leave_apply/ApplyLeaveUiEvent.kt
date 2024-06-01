package com.poulastaa.lms.presentation.leave_apply

import android.content.Context
import android.net.Uri

sealed interface ApplyLeaveUiEvent {
    data class OnLeaveTypeSelected(val index: Int) : ApplyLeaveUiEvent
    data class OnDayTypeSelected(val index: Int) : ApplyLeaveUiEvent

    data class OnFromDateSelected(val date: String) : ApplyLeaveUiEvent
    data class OnToDateSelected(val date: String) : ApplyLeaveUiEvent

    data class OnLeaveReason(val text: String) : ApplyLeaveUiEvent

    data class OnAddressDuringLeaveSelected(val index: Int) : ApplyLeaveUiEvent
    data class OnAddressDuringLeaveOther(val value: String) : ApplyLeaveUiEvent
    data class OnPathSelected(val index: Int) : ApplyLeaveUiEvent

    data class OnDocSelected(val url: Uri?) : ApplyLeaveUiEvent

    data class OnReqClick(val context: Context) : ApplyLeaveUiEvent

    data object OnLeaveTypeToggle : ApplyLeaveUiEvent
    data object OnDayTypeToggle : ApplyLeaveUiEvent
    data object OnPathToggle : ApplyLeaveUiEvent
    data object OnAddressDuringLeaveToggle : ApplyLeaveUiEvent
    data object OnAddressDuringLeaveOutSideBackClick : ApplyLeaveUiEvent

    data object OnFromDateToggle : ApplyLeaveUiEvent
    data object OnToDateToggle : ApplyLeaveUiEvent
}
package com.poulastaa.lms.presentation.leave_approval

sealed interface ApproveLeaveUiEvent {
    data class OnItemToggle(val id: Int) : ApproveLeaveUiEvent

    data object OnActionToggle : ApproveLeaveUiEvent
    data class OnActionSelect(val index: Int) : ApproveLeaveUiEvent
    data class OnCauseChange(val value: String) : ApproveLeaveUiEvent
    data object OnConformClick : ApproveLeaveUiEvent
}
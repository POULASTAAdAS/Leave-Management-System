package com.poulastaa.lms.presentation.leave_approval

sealed interface ApproveLeaveUiEvent {
    data class OnItemToggle(val id: Long) : ApproveLeaveUiEvent

    data class OnExpandDocToggle(val id: Long) : ApproveLeaveUiEvent
    data class OnImageFocusToggle(val id: Long) : ApproveLeaveUiEvent

    data class OnActionToggle(val id: Long) : ApproveLeaveUiEvent
    data class OnActionSelect(val index: Int, val id: Long) : ApproveLeaveUiEvent
    data class OnCauseChange(val value: String, val id: Long) : ApproveLeaveUiEvent
    data class OnConformClick(val item: LeaveApproveCardInfo) : ApproveLeaveUiEvent
}
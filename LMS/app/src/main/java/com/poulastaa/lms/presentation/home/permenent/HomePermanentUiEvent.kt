package com.poulastaa.lms.presentation.home.permenent

sealed interface HomePermanentUiEvent {
    data object OnApplyLeaveClick : HomePermanentUiEvent
    data object OnLeaveStatusClick : HomePermanentUiEvent
    data object OnLeaveHistoryClick : HomePermanentUiEvent

    data object OnProfilePicClick : HomePermanentUiEvent

    data object OnApproveLeaveClick : HomePermanentUiEvent
    data object OnViewLeaveClick : HomePermanentUiEvent
    data object OnViewReportClick : HomePermanentUiEvent
}
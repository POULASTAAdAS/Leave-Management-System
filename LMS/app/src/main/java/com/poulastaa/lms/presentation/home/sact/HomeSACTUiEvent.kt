package com.poulastaa.lms.presentation.home.sact

sealed interface HomeSACTUiEvent {
    data object OnApplyLeaveClick : HomeSACTUiEvent
    data object OnLeaveStatusClick : HomeSACTUiEvent
    data object OnLeaveHistoryClick : HomeSACTUiEvent

    data object OnProfilePicClick : HomeSACTUiEvent
}
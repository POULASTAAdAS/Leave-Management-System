package com.poulastaa.lms.presentation.home.head_clark

sealed interface HeadClarkUiEvent {
    data object OnProfileClick : HeadClarkUiEvent

    data object OnApproveLeaveClick : HeadClarkUiEvent
    data object OnViewLeaveClick : HeadClarkUiEvent
    data object OnDownloadReportClick : HeadClarkUiEvent
}
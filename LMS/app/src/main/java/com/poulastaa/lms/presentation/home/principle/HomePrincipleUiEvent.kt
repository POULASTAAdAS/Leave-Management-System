package com.poulastaa.lms.presentation.home.principle

sealed interface HomePrincipleUiEvent {
    data object OnProfilePicClick : HomePrincipleUiEvent

    data object OnApplyLeaveClick : HomePrincipleUiEvent
    data object OnLeaveStatusClick : HomePrincipleUiEvent
    data object OnLeaveHistoryClick : HomePrincipleUiEvent

    data object OnDefineDepartmentInChargeClick : HomePrincipleUiEvent
    data object OnApproveLeaveClick : HomePrincipleUiEvent
    data object OnAddClick : HomePrincipleUiEvent

    data object OnViewLeaveClick : HomePrincipleUiEvent
    data object OnViewReportClick : HomePrincipleUiEvent
}
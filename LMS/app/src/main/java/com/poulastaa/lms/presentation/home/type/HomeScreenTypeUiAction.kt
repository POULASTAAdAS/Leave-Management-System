package com.poulastaa.lms.presentation.home.type

sealed interface HomeScreenTypeUiAction {
    data object Err : HomeScreenTypeUiAction
}
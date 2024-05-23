package com.poulastaa.lms.presentation.home

import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.UiText

sealed interface HomeUiAction {
    data class OnNavigate(val screens: Screens) : HomeUiAction
    data class ShowToast(val message: UiText) : HomeUiAction
}
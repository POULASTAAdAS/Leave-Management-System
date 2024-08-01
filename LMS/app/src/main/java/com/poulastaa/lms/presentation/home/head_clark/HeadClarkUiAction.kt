package com.poulastaa.lms.presentation.home.head_clark

import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.UiText

sealed interface HeadClarkUiAction {
    data class OnNavigate(val screens: Screens) : HeadClarkUiAction
    data class ShowToast(val message: UiText) : HeadClarkUiAction
}
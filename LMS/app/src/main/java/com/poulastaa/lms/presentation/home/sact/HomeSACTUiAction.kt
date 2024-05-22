package com.poulastaa.lms.presentation.home.sact

import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.UiText

sealed interface HomeSACTUiAction {
    data class OnNavigate(val screens: Screens) : HomeSACTUiAction
    data class ShowToast(val message: UiText) : HomeSACTUiAction
}
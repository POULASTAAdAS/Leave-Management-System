package com.poulastaa.lms.presentation.profile

import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.UiText

sealed interface ProfileUiAction {
    data class OnNavigate(
        val screen: Screens,
        val args: Map<String, String> = emptyMap()
    ) : ProfileUiAction

    data class ShowToast(val message: UiText) : ProfileUiAction
}
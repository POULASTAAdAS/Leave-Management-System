package com.poulastaa.lms.presentation.profile.edit.details_edit

import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.UiText

sealed interface DetailsEditUiAction {
    data class OnSuccess(val screens: Screens) : DetailsEditUiAction
    data class ShowToast(val message: UiText) : DetailsEditUiAction
}
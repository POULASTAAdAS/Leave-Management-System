package com.poulastaa.lms.presentation.profile.edit.address_edit

import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.UiText

sealed interface AddressEditUiAction {
    data class ShowToast(val message: UiText) : AddressEditUiAction
}
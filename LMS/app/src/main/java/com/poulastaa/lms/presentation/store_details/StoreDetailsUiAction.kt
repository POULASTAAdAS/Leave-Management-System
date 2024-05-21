package com.poulastaa.lms.presentation.store_details

import com.poulastaa.lms.ui.utils.UiText

sealed interface StoreDetailsUiAction {
    data object OnSuccess : StoreDetailsUiAction
    data class ShowToast(val message: UiText) : StoreDetailsUiAction
}
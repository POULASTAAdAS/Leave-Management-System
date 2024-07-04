package com.poulastaa.lms.presentation.profile

import android.content.Context
import android.net.Uri

sealed interface ProfileUiEvent {
    data class OnProfileEditClick(val context: Context, val uri: Uri?) : ProfileUiEvent
    data object DetailsEditClick : ProfileUiEvent

    data object OnPresentAddressEditClick : ProfileUiEvent
    data object OnHomeAddressEditClick : ProfileUiEvent

    data object LogOut : ProfileUiEvent
}
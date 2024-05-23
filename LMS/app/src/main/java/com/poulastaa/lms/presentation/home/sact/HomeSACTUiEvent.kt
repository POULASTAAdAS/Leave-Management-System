package com.poulastaa.lms.presentation.home.sact

import android.content.Context
import android.net.Uri

sealed interface HomeSACTUiEvent {
    data object OnApplyLeaveClick : HomeSACTUiEvent
    data object OnLeaveStatusClick : HomeSACTUiEvent
    data object OnLeaveHistoryClick : HomeSACTUiEvent

    data class OnProfilePicClick(
        val context: Context,
        val url: Uri? = null
    ) : HomeSACTUiEvent
}
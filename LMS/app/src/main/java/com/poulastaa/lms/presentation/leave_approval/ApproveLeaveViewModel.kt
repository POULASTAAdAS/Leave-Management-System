package com.poulastaa.lms.presentation.leave_approval

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.presentation.leave_history.LeaveHistoryUiAction
import com.poulastaa.lms.ui.utils.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

class ApproveLeaveViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(ApproveLeaveUiState())
        private set

    init {
        viewModelScope.launch {
            val user = ds.readUser().first()

            if (user.userType == UserType.PRINCIPLE) {
                state = state.copy(
                    actions = state.actions.copy(
                        all = listOf(
                            "Approve",
                            "Reject"
                        )
                    )
                )
            }
        }
    }

    private val _uiEvent = Channel<ApproveLeaveUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: ApproveLeaveUiEvent) {
        when (event) {
            is ApproveLeaveUiEvent.OnItemToggle -> {

                state = state.copy(
                    data = state.data.map {
                        if (it.id == event.id) it.copy(isSelected = !it.isSelected)
                        else it
                    }
                )
            }

            ApproveLeaveUiEvent.OnActionToggle -> {
                state = state.copy(
                    actions = state.actions.copy(
                        isDialogOpen = !state.actions.isDialogOpen
                    )
                )
            }

            is ApproveLeaveUiEvent.OnActionSelect -> {
                state = state.copy(
                    actions = state.actions.copy(
                        isDialogOpen = false,
                        selected = state.actions.all[event.index],
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is ApproveLeaveUiEvent.OnCauseChange -> {
                state = state.copy(
                    cause = state.cause.copy(
                        data = event.value,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            ApproveLeaveUiEvent.OnConformClick -> {
                if (state.isMakingApiCall) return

                state = state.copy(
                    isMakingApiCall = true
                )
            }
        }
    }

    fun getInitialValue() {

    }
}
package com.poulastaa.lms.presentation.home.permenent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.lms.domain.repository.utils.ConnectivityObserver
import com.poulastaa.lms.presentation.home.HomeUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePermanentViewModel @Inject constructor(
    private val network: ConnectivityObserver,
) : ViewModel() {
    var state by mutableStateOf(HomePermanentUiState())
        private set

    init {
        viewModelScope.launch {
            network.observe().collectLatest {
                state = if (it == ConnectivityObserver.NetworkStatus.AVAILABLE) state.copy(
                    isInternet = true
                ) else state.copy(
                    isInternet = false
                )
            }
        }
    }

    private val _uiEvent = Channel<HomeUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: HomePermanentUiEvent) {
        when (event) {
            HomePermanentUiEvent.OnProfilePicClick -> {

            }

            HomePermanentUiEvent.OnApplyLeaveClick -> {

            }

            HomePermanentUiEvent.OnLeaveHistoryClick -> {

            }

            HomePermanentUiEvent.OnLeaveStatusClick -> {

            }

            HomePermanentUiEvent.OnApproveLeaveClick -> {

            }

            HomePermanentUiEvent.OnViewReportClick -> {

            }

            HomePermanentUiEvent.OnViewLeaveClick -> {

            }
        }
    }
}
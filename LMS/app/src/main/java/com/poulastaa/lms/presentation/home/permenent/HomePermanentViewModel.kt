package com.poulastaa.lms.presentation.home.permenent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.presentation.home.HomeUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePermanentViewModel @Inject constructor() : ViewModel() {
    private val _uiEvent = Channel<HomeUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: HomePermanentUiEvent) {
        when (event) {
            HomePermanentUiEvent.OnApplyLeaveClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.ApplyLeave))
                }
            }

            HomePermanentUiEvent.OnLeaveHistoryClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.LeaveHistory))
                }
            }

            HomePermanentUiEvent.OnLeaveStatusClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.LeaveStatus))
                }
            }

            HomePermanentUiEvent.OnProfilePicClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.Profile))
                }
            }

            HomePermanentUiEvent.OnApproveLeaveClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.ApproveLeave))
                }
            }

            HomePermanentUiEvent.OnViewLeaveClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.ViewLeave))
                }
            }

            HomePermanentUiEvent.OnViewReportClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.ViewReport))
                }
            }
        }
    }
}
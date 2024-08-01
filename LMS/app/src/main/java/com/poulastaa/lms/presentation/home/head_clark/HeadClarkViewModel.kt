package com.poulastaa.lms.presentation.home.head_clark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.lms.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeadClarkViewModel @Inject constructor() : ViewModel() {
    private val _uiEvent = Channel<HeadClarkUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: HeadClarkUiEvent) {
        when (event) {
            HeadClarkUiEvent.OnProfileClick -> viewModelScope.launch {
                _uiEvent.send(HeadClarkUiAction.OnNavigate(Screens.Profile))
            }

            HeadClarkUiEvent.OnApproveLeaveClick -> viewModelScope.launch {
                _uiEvent.send(HeadClarkUiAction.OnNavigate(Screens.ApproveLeave))
            }

            HeadClarkUiEvent.OnViewLeaveClick -> viewModelScope.launch {
                _uiEvent.send(HeadClarkUiAction.OnNavigate(Screens.ViewLeave))
            }

            HeadClarkUiEvent.OnDownloadReportClick ->viewModelScope.launch {
                _uiEvent.send(HeadClarkUiAction.OnNavigate(Screens.DownloadReport))
            }
        }
    }
}
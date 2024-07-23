package com.poulastaa.lms.presentation.home.principle

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
class HomePrincipleViewModel @Inject constructor() : ViewModel() {
    private val _uiEvent = Channel<HomeUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: HomePrincipleUiEvent) {
        when (event) {
            HomePrincipleUiEvent.OnApplyLeaveClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.ApplyLeave))
                }
            }

            HomePrincipleUiEvent.OnLeaveHistoryClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.LeaveHistory))
                }
            }

            HomePrincipleUiEvent.OnLeaveStatusClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.LeaveStatus))
                }
            }

            HomePrincipleUiEvent.OnProfilePicClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.Profile))
                }
            }


            HomePrincipleUiEvent.OnDefineDepartmentInChargeClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.DefineDepartmentInCharge))
                }
            }

            HomePrincipleUiEvent.OnApproveLeaveClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.ApproveLeave))
                }
            }

            HomePrincipleUiEvent.OnAddClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.Add))
                }
            }


            HomePrincipleUiEvent.OnViewLeaveClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.ViewLeave))
                }
            }


            HomePrincipleUiEvent.OnUpdateLeaveBalanceClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.UpdateLeaveBalance))
                }
            }


            HomePrincipleUiEvent.OnViewReportClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.ViewReport))
                }
            }
        }
    }
}
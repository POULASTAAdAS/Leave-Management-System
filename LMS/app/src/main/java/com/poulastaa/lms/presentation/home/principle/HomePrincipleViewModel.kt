package com.poulastaa.lms.presentation.home.principle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.presentation.home.HomeUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

@HiltViewModel
class HomePrincipleViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient,
) : ViewModel() {
    var state by mutableStateOf(HomePrincipalUiState())
        private set

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
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.DownloadReport))
                }
            }

            HomePrincipleUiEvent.OnRemoveEmployeeClick -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.RemoveEmployee))
                }
            }
        }
    }
}
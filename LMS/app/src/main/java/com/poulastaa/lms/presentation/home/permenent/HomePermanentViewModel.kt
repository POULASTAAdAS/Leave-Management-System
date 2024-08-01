package com.poulastaa.lms.presentation.home.permenent

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
class HomePermanentViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient,
) : ViewModel() {
    private val _uiEvent = Channel<HomeUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

//    init {
//        viewModelScope.launch { // validate on open
//            val user = ds.readUser().first()
//            val cookie = ds.readCookie().first()
//
//            when (user.userType) {
//                UserType.PERMANENT -> {
//                    val res = client.get<Boolean>(
//                        route = EndPoints.IsStillDepartmentHead.route,
//                        params = listOf(),
//                        gson = gson,
//                        cookie = cookie,
//                        cookieManager = cookieManager,
//                        ds = ds
//                    )
//
//                    if (res is Result.Success) {
//                        if (res.data && !user.isDepartmentInCharge) {
//                            clearDs(ds)
//                            _uiEvent.send(HomeUiAction.ShowToast(UiText.StringResource(R.string.promoted_to_department_head)))
//
//                            _uiEvent.send(HomeUiAction.OnNavigate(Screens.Auth))
//                        }
//
//                        if (!res.data && user.isDepartmentInCharge) {
//                            clearDs(ds)
//                            _uiEvent.send(HomeUiAction.ShowToast(UiText.StringResource(R.string.demoted_from_department_head)))
//
//                            _uiEvent.send(HomeUiAction.OnNavigate(Screens.Auth))
//                        }
//                    }
//                }
//
//                else -> {
//                    Unit
//                }
//            }
//        }
//    }

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
                    _uiEvent.send(HomeUiAction.OnNavigate(Screens.DownloadReport))
                }
            }
        }
    }
}
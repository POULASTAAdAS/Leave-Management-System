package com.poulastaa.lms.presentation.leave_status

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.data.model.leave.GetBalanceRes
import com.poulastaa.lms.data.remote.get
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.ui.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

@HiltViewModel
class LeaveStatusViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val gson: Gson,
    private val cookieManager: CookieManager,
    val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(LeaveStatusUiState())
        private set

    init {
        viewModelScope.launch {
            val user = ds.readUser().first()

            when (user.userType) {
                UserType.PERMANENT -> {
                    state = state.copy(
                        leaveTypes = state.leaveTypes.copy(
                            all = listOf(
                                "Casual Leave",
                                "Commuted Leave",
                                "Compensatory Leave",
                                "Earned Leave",
                                "Extraordinary Leave",
                                "Leave Not Due",
                                "Maternity Leave",
                                "Medical Leave",
                                "On Duty Leave",
                                "Quarintine Leave",
                                "Special Disability Leave",
                                "Special Study Leave",
                                "Study Leave"
                            )
                        )
                    )
                }

                UserType.SACT -> {
                    state = state.copy(
                        leaveTypes = state.leaveTypes.copy(
                            all = listOf(
                                "Casual Leave",
                                "Medical Leave",
                                "Study Leave"
                            )
                        )
                    )
                }

                else -> Unit
            }
        }
    }

    private val _uiEvent = Channel<LeaveStatusUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()


    private var getLeaveBalance: Job? = null

    fun onEvent(event: LeaveStatusUiEvent) {
        when (event) {
            LeaveStatusUiEvent.OnLeaveTypeToggle -> {
                state = state.copy(
                    leaveTypes = state.leaveTypes.copy(
                        isDialogOpen = !state.leaveTypes.isDialogOpen
                    )
                )
            }

            is LeaveStatusUiEvent.OnLeaveTypeSelected -> {
                viewModelScope.launch(Dispatchers.IO) {
                    getLeaveBalance?.cancel()
                    getLeaveBalance = getLeaveBalance()
                }
            }
        }
    }

    private suspend fun getLeaveBalance() = viewModelScope.launch(Dispatchers.IO) {
        state = state.copy(
            isMakingApiCall = true
        )

        val cookie = ds.readCookie().first()

        val response = client.get<GetBalanceRes>(
            route = EndPoints.GetLeaveBalance.route,
            params = listOf(
                "type" to state.leaveTypes.selected
            ),
            gson = gson,
            cookie = cookie,
            cookieManager = cookieManager,
            ds = ds
        )

        when (response) {
            is Result.Error -> {
                when (response.error) {
                    DataError.Network.NO_INTERNET -> {
                        _uiEvent.send(
                            LeaveStatusUiAction.OnErr(
                                UiText.StringResource(R.string.error_internet)
                            )
                        )
                    }

                    else -> {
                        _uiEvent.send(
                            LeaveStatusUiAction.OnErr(
                                UiText.StringResource(R.string.error_something_went_wrong)
                            )
                        )
                    }
                }
            }

            is Result.Success -> {
                state = state.copy(
                    balance = response.data.balance,
                    isMakingApiCall = false
                )
            }
        }
    }
}
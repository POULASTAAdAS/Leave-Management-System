package com.poulastaa.lms.presentation.apply_leave

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
import com.poulastaa.lms.domain.repository.utils.ConnectivityObserver
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
class ApplyLeaveViewModel @Inject constructor(
    private val network: ConnectivityObserver,
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(ApplyLeaveUiState())
        private set

    private val _uiEvent = Channel<ApplyLeaveUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            when (ds.readUser().first().userType) {
                UserType.PERMANENT -> {
                    state = state.copy(
                        leaveType = state.leaveType.copy(
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
                        ),
                        path = state.path.copy(
                            all = listOf(
                                "Principal",
                                "Head Clark"
                            )
                        )
                    )
                }

                UserType.SACT -> {
                    state = state.copy(
                        leaveType = state.leaveType.copy(
                            all = listOf(
                                "Casual Leave",
                                "Medical Leave",
                                "Study Leave"
                            )
                        ),
                        path = state.path.copy(
                            all = listOf(
                                "Department Head",
                                "Head Clark"
                            )
                        )
                    )
                }

                else -> Unit
            }
        }
    }

    private var getLeaveBalance: Job? = null

    fun onEvent(event: ApplyLeaveUiEvent) {
        when (event) {
            is ApplyLeaveUiEvent.OnLeaveTypeSelected -> {
                state = state.copy(
                    leaveType = state.leaveType.copy(
                        isDialogOpen = !state.leaveType.isDialogOpen,
                        selected = state.leaveType.all[event.index]
                    )
                )

                viewModelScope.launch {
                    getLeaveBalance?.cancel()
                    getLeaveBalance = getLeaveBalance()
                }
            }

            is ApplyLeaveUiEvent.OnDayTypeSelected -> {
                state = state.copy(
                    dayType = state.dayType.copy(
                        isDialogOpen = !state.dayType.isDialogOpen,
                        selected = state.dayType.all[event.index]
                    )
                )
            }

            is ApplyLeaveUiEvent.OnFromDateSelected -> {
                state = state.copy(
                    fromDate = state.fromDate.copy(
                        isDialogOpen = !state.fromDate.isDialogOpen,
                        data = event.date,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is ApplyLeaveUiEvent.OnToDateSelected -> {
                state = state.copy(
                    toDate = state.toDate.copy(
                        isDialogOpen = !state.toDate.isDialogOpen,
                        data = event.date,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is ApplyLeaveUiEvent.OnLeaveReason -> {
                state = state.copy(
                    leaveReason = state.leaveReason.copy(
                        data = event.text,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is ApplyLeaveUiEvent.OnAddressDuringLeaveSelected -> {
                state = state.copy(
                    addressDuringLeave = state.addressDuringLeave.copy(
                        isDialogOpen = !state.addressDuringLeave.isDialogOpen,
                        selected = state.addressDuringLeave.all[event.index]
                    )
                )
            }

            is ApplyLeaveUiEvent.OnAddressDuringLeaveOther -> {
                state = state.copy(
                    addressDuringLeaveOutStation = state.addressDuringLeaveOutStation.copy(
                        data = event.value,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is ApplyLeaveUiEvent.OnPathSelected -> {
                state = state.copy(
                    path = state.path.copy(
                        isDialogOpen = !state.path.isDialogOpen,
                        selected = state.path.all[event.index]
                    )
                )
            }

            ApplyLeaveUiEvent.OnReqClick -> {
                if (state.isMakingApiCall) return

                state = state.copy(
                    isMakingApiCall = true
                )
            }

            ApplyLeaveUiEvent.OnDayTypeToggle -> {
                state = state.copy(
                    dayType = state.dayType.copy(
                        isDialogOpen = !state.dayType.isDialogOpen
                    )
                )
            }


            ApplyLeaveUiEvent.OnLeaveTypeToggle -> {
                state = state.copy(
                    leaveType = state.leaveType.copy(
                        isDialogOpen = !state.leaveType.isDialogOpen
                    )
                )
            }

            ApplyLeaveUiEvent.OnPathToggle -> {
                state = state.copy(
                    path = state.path.copy(
                        isDialogOpen = !state.path.isDialogOpen
                    )
                )
            }

            ApplyLeaveUiEvent.OnAddressDuringLeaveToggle -> {
                state = state.copy(
                    addressDuringLeave = state.addressDuringLeave.copy(
                        isDialogOpen = !state.addressDuringLeave.isDialogOpen
                    )
                )
            }

            ApplyLeaveUiEvent.OnAddressDuringLeaveOutSideBackClick -> {
                state = state.copy(
                    addressDuringLeave = state.addressDuringLeave.copy(
                        selected = ""
                    )
                )
            }

            ApplyLeaveUiEvent.OnFromDateToggle -> {
                state = state.copy(
                    fromDate = state.fromDate.copy(
                        isDialogOpen = !state.fromDate.isDialogOpen
                    )
                )
            }

            ApplyLeaveUiEvent.OnToDateToggle -> {
                state = state.copy(
                    toDate = state.toDate.copy(
                        isDialogOpen = !state.toDate.isDialogOpen
                    )
                )
            }
        }
    }

    private suspend fun getLeaveBalance() = viewModelScope.launch(Dispatchers.IO) {
        state = state.copy(
            isGettingLeaveBalance = true
        )

        val cookie = ds.readCookie().first()

        val response = client.get<GetBalanceRes>(
            route = EndPoints.GetLeaveBalance.route,
            params = listOf(
                "type" to state.leaveType.selected
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
                            ApplyLeaveUiAction.Err(
                                UiText.StringResource(R.string.error_internet)
                            )
                        )
                    }

                    else -> {
                        _uiEvent.send(
                            ApplyLeaveUiAction.Err(
                                UiText.StringResource(R.string.error_something_went_wrong)
                            )
                        )
                    }
                }
            }

            is Result.Success -> {
                state = state.copy(
                    balance = response.data.balance,
                    isGettingLeaveBalance = false
                )
            }
        }
    }
}
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
import com.poulastaa.lms.data.model.leave.ApplyLeaveReq
import com.poulastaa.lms.data.model.leave.ApplyLeaveRes
import com.poulastaa.lms.data.model.leave.ApplyLeaveStatus
import com.poulastaa.lms.data.model.leave.GetBalanceRes
import com.poulastaa.lms.data.remote.get
import com.poulastaa.lms.data.remote.post
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.ui.utils.DateUtils
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ApplyLeaveViewModel @Inject constructor(
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

                if (state.leaveType.selected == "Medical Leave" || state.leaveType.selected == "Study Leave") {
                    state = state.copy(
                        isDocNeeded = true
                    )

                    viewModelScope.launch {
                        _uiEvent.send(
                            ApplyLeaveUiAction.Err(UiText.StringResource(R.string.please_attach_appropriate_doc))
                        )
                    }
                }
            }

            is ApplyLeaveUiEvent.OnFromDateSelected -> {
                val today = LocalDate.now()
                val fromDate =
                    LocalDate.parse(event.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                if (fromDate < today) {
                    viewModelScope.launch {
                        _uiEvent.send(ApplyLeaveUiAction.Err(UiText.StringResource(R.string.please_select_a_valid_date)))
                    }

                    return
                }

                if (state.toDate.data.isNotEmpty()) {
                    val toDate = LocalDate.parse(
                        state.toDate.data,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )

                    if (fromDate > toDate) {
                        viewModelScope.launch {
                            _uiEvent.send(ApplyLeaveUiAction.Err(UiText.StringResource(R.string.please_select_a_valid_date_range)))
                        }

                        return
                    }

                    state = state.copy(
                        totalDays = DateUtils.calculateTotalDays(
                            fromDate = fromDate,
                            toDate = toDate
                        )
                    )
                }

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
                val curDate = LocalDate.now()
                val toDate =
                    LocalDate.parse(event.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))


                if (toDate < curDate) {
                    viewModelScope.launch {
                        _uiEvent.send(ApplyLeaveUiAction.Err(UiText.StringResource(R.string.please_select_a_valid_date)))
                    }

                    return
                }

                val fromDate =
                    LocalDate.parse(state.fromDate.data, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                if (fromDate > toDate) {
                    viewModelScope.launch {
                        _uiEvent.send(ApplyLeaveUiAction.Err(UiText.StringResource(R.string.please_select_a_valid_date_range)))
                    }

                    return
                }

                state = state.copy(
                    toDate = state.toDate.copy(
                        isDialogOpen = !state.toDate.isDialogOpen,
                        data = event.date,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    ),
                    totalDays = DateUtils.calculateTotalDays(
                        fromDate = fromDate,
                        toDate = toDate
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
                if (state.fromDate.data.isEmpty()) {
                    viewModelScope.launch {
                        _uiEvent.send(ApplyLeaveUiAction.Err(UiText.StringResource(R.string.please_select_from_date_first)))
                    }

                    return
                }

                state = state.copy(
                    toDate = state.toDate.copy(
                        isDialogOpen = !state.toDate.isDialogOpen
                    )
                )
            }

            ApplyLeaveUiEvent.OnReqClick -> {
                if (state.isMakingApiCall) return

                if (isErr()) return

                state = state.copy(
                    isMakingApiCall = true
                )

                applyLeave()
            }
        }
    }

    private fun isErr(): Boolean {
        var isErr = false

        if (state.leaveType.selected.isEmpty()) {
            viewModelScope.launch {
                state = state.copy(
                    leaveType = state.leaveType.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            }

            isErr = true
        }

        if (state.fromDate.data.isEmpty()) {
            viewModelScope.launch {
                state = state.copy(
                    fromDate = state.fromDate.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            }

            isErr = true
        }

        if (state.toDate.data.isEmpty()) {
            viewModelScope.launch {
                state = state.copy(
                    toDate = state.toDate.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            }

            isErr = true
        }

        if (state.totalDays.toDouble() > state.balance.toDouble()) {
            viewModelScope.launch {
                _uiEvent.send(
                    ApplyLeaveUiAction.Err(UiText.StringResource(R.string.not_enough_balance))
                )
            }

            isErr = true
        }


        if (state.leaveReason.data.isEmpty()) {
            viewModelScope.launch {
                state = state.copy(
                    leaveReason = state.leaveReason.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            }

            isErr = true
        }

        if (state.addressDuringLeave.selected == "OutStation" &&
            state.addressDuringLeaveOutStation.data.isEmpty()
        ) {
            viewModelScope.launch {
                state = state.copy(
                    addressDuringLeaveOutStation = state.addressDuringLeaveOutStation.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            }

            isErr = true
        } else if (state.addressDuringLeave.selected.isEmpty()) {
            viewModelScope.launch {
                state = state.copy(
                    addressDuringLeave = state.addressDuringLeave.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            }

            isErr = true
        }


        if (state.path.selected.isEmpty()) {
            viewModelScope.launch {
                state = state.copy(
                    path = state.path.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            }

            isErr = true
        }

        if (isErr) {
            viewModelScope.launch {
                _uiEvent.send(
                    ApplyLeaveUiAction.Err(UiText.StringResource(R.string.error_field_are_empty_or_invalid))
                )
            }
        }

        return isErr
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

    private fun applyLeave() {
        viewModelScope.launch(Dispatchers.IO) {
            val cookie = ds.readCookie().first()
            val user = ds.readUser().first()

            val body = ApplyLeaveReq(
                email = user.email,
                leaveType = state.leaveType.selected,
                fromDate = state.fromDate.data,
                toDate = state.toDate.data,
                reason = state.leaveReason.data,
                addressDuringLeave = state.addressDuringLeave.selected.ifEmpty { state.addressDuringLeaveOutStation.data },
                path = state.path.selected
            )

            val response = client.post<ApplyLeaveReq, ApplyLeaveRes>(
                route = EndPoints.ApplyLeave.route,
                body = body,
                gson = gson,
                cookieManager = cookieManager,
                cookie = cookie,
                ds = ds
            )

            when (response) {
                is Result.Error -> {
                    when (response.error) {
                        DataError.Network.NO_INTERNET -> {
                            _uiEvent.send(ApplyLeaveUiAction.Err(UiText.StringResource(R.string.error_internet)))
                        }

                        else -> {
                            _uiEvent.send(ApplyLeaveUiAction.Err(UiText.StringResource(R.string.error_something_went_wrong)))
                        }
                    }
                }

                is Result.Success -> {
                    when (response.data.status) {
                        ApplyLeaveStatus.ACCEPTED -> {
                            _uiEvent.send(ApplyLeaveUiAction.Err(UiText.StringResource(R.string.leave_req_accepted)))

                            if (state.leaveType.selected == body.leaveType) state = state.copy(
                                balance = response.data.newBalance
                            )
                        }

                        ApplyLeaveStatus.REJECTED -> _uiEvent.send(
                            ApplyLeaveUiAction.Err(
                                UiText.StringResource(
                                    R.string.leave_req_rejected
                                )
                            )
                        )

                        ApplyLeaveStatus.SOMETHING_WENT_WRONG -> _uiEvent.send(
                            ApplyLeaveUiAction.Err(
                                UiText.StringResource(R.string.error_something_went_wrong)
                            )
                        )
                    }
                }
            }


            state = state.copy(
                isMakingApiCall = false
            )
        }
    }
}
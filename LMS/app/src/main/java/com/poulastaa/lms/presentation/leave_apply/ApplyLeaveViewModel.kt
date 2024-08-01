package com.poulastaa.lms.presentation.leave_apply

import android.content.Context
import android.os.Build
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
import com.poulastaa.lms.data.remote.applyLeave
import com.poulastaa.lms.data.remote.get
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.presentation.utils.fileFromUri
import com.poulastaa.lms.ui.utils.DateUtils
import com.poulastaa.lms.ui.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
    private val client: OkHttpClient,
) : ViewModel() {
    var state by mutableStateOf(ApplyLeaveUiState())
        private set

    private val _uiEvent = Channel<ApplyLeaveUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val user = ds.readUser().first()

            when (user.userType) {
                UserType.PERMANENT -> {
                    state = state.copy(
                        leaveType = state.leaveType.copy(
                            all = listOf(
                                "Casual Leave(CL)",
                                "Commuted Leave(CL)",
                                "Compensatory Leave(CL)",
                                "Earned Leave(EL)",
                                "Extraordinary Leave(EL)",
                                "Leave Not Due(LND)",
                                "Maternity Leave(ML)",
                                "Medical Leave(ML)",
                                "On Duty Leave(OD)",
                                "Quarintine Leave(QL)",
                                "Special Disability Leave(SDL)",
                                "Special Study Leave(SSL)",
                                "Study Leave(SL)"
                            )
                        ),
                        path = if (user.isDepartmentInCharge) state.path.copy(
                            all = listOf(
                                "Principal"
                            )
                        ) else if (user.department == "NST"){
                            state.path.copy(
                                all = listOf(
                                    "Head Clark"
                                )
                            )
                        }else state.path.copy(
                            all = listOf(
                                "Departmental In-Charge"
                            )
                        )
                    )
                }

                UserType.SACT -> {
                    state = state.copy(
                        leaveType = state.leaveType.copy(
                            all = listOf(
                                "Casual Leave(CL)",
                                "Medical Leave(ML)",
                                "Study Leave(SL)",
                                "On Duty Leave(OD)"
                            )
                        ),
                        path = state.path.copy(
                            all = listOf(
                                "Departmental In-Charge"
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

                state =
                    if (state.leaveType.selected != "Casual Leave(CL)" && state.leaveType.selected != "On Duty Leave(OD)") state.copy(
                        isDocNeeded = true
                    )
                    else state.copy(
                        isDocNeeded = false
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val today =
                        LocalDate.now()
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
            }

            is ApplyLeaveUiEvent.OnToDateSelected -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                        LocalDate.parse(
                            state.fromDate.data,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )

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

            is ApplyLeaveUiEvent.OnDocSelected -> {
                state = state.copy(
                    docUrl = event.url,
                    isDocErr = false
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

            is ApplyLeaveUiEvent.OnReqClick -> {
                if (state.isMakingApiCall) return

                if (isErr()) return

                state = state.copy(
                    isMakingApiCall = true
                )

                applyLeave(event.context)
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

        if (state.addressDuringLeave.selected == "Out Station Address" &&
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

        if (state.isDocNeeded && state.docUrl == null) {
            state = state.copy(
                isDocErr = true
            )

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
                "type" to state.leaveType.selected.split('(')[0]
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

    private fun applyLeave(
        context: Context,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val cookie = ds.readCookie().first()
            val user = ds.readUser().first()

            val body = ApplyLeaveReq(
                email = user.email,
                leaveType = state.leaveType.selected.trim().split('(')[0],
                fromDate = state.fromDate.data.trim(),
                toDate = state.toDate.data.trim(),
                totalDays = state.totalDays.trim(),
                reason = state.leaveReason.data.trim(),
                addressDuringLeave = state.addressDuringLeave.selected.trim(),
                path = if (state.path.selected == "Departmental In-Charge") "Department Head" else state.path.selected,
            )

            val file = state.docUrl?.let {
                fileFromUri(
                    context = context,
                    uri = it,
                    type = "ApplyLeave"
                )
            }

            val response = client.applyLeave<ApplyLeaveReq, ApplyLeaveRes>(
                route = EndPoints.ApplyLeave.route,
                cookieManager = cookieManager,
                ds = ds,
                body = body,
                gson = gson,
                cookie = cookie,
                file = file
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
                    when (response.data.status) {
                        ApplyLeaveStatus.ACCEPTED -> {
                            state = state.copy(
                                balance = response.data.newBalance,
                                isSuccess = true
                            )

                            _uiEvent.send(
                                ApplyLeaveUiAction.Err(
                                    UiText.StringResource(R.string.leave_req_accepted)
                                )
                            )

                            for (i in state.returnCounter downTo 0) {
                                delay(1000)

                                state = state.copy(
                                    returnCounter = i
                                )
                            }

                            _uiEvent.send(ApplyLeaveUiAction.NavigateBack)
                        }

                        ApplyLeaveStatus.REJECTED -> {
                            _uiEvent.send(
                                ApplyLeaveUiAction.Err(
                                    UiText.StringResource(R.string.leave_req_rejected)
                                )
                            )
                        }

                        ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS -> {
                            _uiEvent.send(
                                ApplyLeaveUiAction.Err(
                                    UiText.StringResource(R.string.leave_req_already_exists)
                                )
                            )
                        }

                        ApplyLeaveStatus.SOMETHING_WENT_WRONG -> {
                            _uiEvent.send(
                                ApplyLeaveUiAction.Err(
                                    UiText.StringResource(R.string.error_something_went_wrong)
                                )
                            )
                        }
                    }
                }
            }


            state = state.copy(
                isMakingApiCall = false
            )
        }
    }
}
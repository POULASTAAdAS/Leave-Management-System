package com.poulastaa.lms.presentation.update_balnce

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.leave.GetDepartmentTeacher
import com.poulastaa.lms.data.model.leave.TeacherLeaveBalance
import com.poulastaa.lms.data.model.leave.UpdateLeaveBalanceReq
import com.poulastaa.lms.data.remote.get
import com.poulastaa.lms.data.remote.post
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.presentation.store_details.ListHolder
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
class UpdateLeaveBalanceViewModel @Inject constructor(
    private val cookieManager: CookieManager,
    private val ds: DataStoreRepository,
    private val gson: Gson,
    private val client: OkHttpClient,
) : ViewModel() {
    var state by mutableStateOf(UpdateBalanceUiState())
        private set

    private val _uiEvent = Channel<UpdateBalanceUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var getDepartmentTeacherJob: Job? = null
    private var getLeaveBalanceJob: Job? = null

    fun onEvent(event: UpdateBalanceUiEvent) {
        when (event) {
            UpdateBalanceUiEvent.OnDepartmentToggle -> {
                state = state.copy(
                    department = state.department.copy(
                        isDialogOpen = !state.department.isDialogOpen
                    )
                )
            }

            is UpdateBalanceUiEvent.OnDepartmentSelected -> {
                state = state.copy(
                    department = state.department.copy(
                        isDialogOpen = false,
                        selected = state.department.all[event.index]
                    )
                )

                viewModelScope.launch(Dispatchers.IO) {
                    getDepartmentTeacherJob?.cancel()
                    getDepartmentTeacherJob = getDepartmentTeacher()
                }
            }

            UpdateBalanceUiEvent.OnTeacherToggle -> {
                state = state.copy(
                    teacher = state.teacher.copy(
                        isDialogOpen = !state.teacher.isDialogOpen
                    )
                )
            }

            is UpdateBalanceUiEvent.OnTeacherSelected -> {
                state = state.copy(
                    teacher = state.teacher.copy(
                        isDialogOpen = false,
                        selected = state.teacher.all[event.index]
                    )
                )

                viewModelScope.launch(Dispatchers.IO) {
                    getLeaveBalanceJob?.cancel()
                    getLeaveBalanceJob = getLeaveBalance()
                }
            }

            UpdateBalanceUiEvent.OnLeaveToggle -> {
                state = state.copy(
                    listOfLeave = state.listOfLeave.copy(
                        isDialogOpen = !state.listOfLeave.isDialogOpen
                    )
                )
            }

            is UpdateBalanceUiEvent.OnLeaveSelected -> {
                val leave = state.listOfLeave.all[event.index]

                val balance = state.responseBalance.first {
                    it.name == leave
                }

                state = state.copy(
                    listOfLeave = state.listOfLeave.copy(
                        isDialogOpen = false,
                        selected = leave,
                    ),
                    isRequestingLeave = true,
                    leaveBalance = balance.balance
                )
            }

            is UpdateBalanceUiEvent.OnLeaveBalanceChange -> {
                state = state.copy(
                    leaveBalance = event.value,
                    isRequestingLeave = true
                )
            }

            UpdateBalanceUiEvent.OnContinueClick -> {
                if (state.isMakingApiCall) return

                val oldBalance = state.responseBalance.firstOrNull {
                    it.name == state.listOfLeave.selected
                } ?: return

                if (state.leaveBalance == oldBalance.balance) {
                    viewModelScope.launch {
                        _uiEvent.send(UpdateBalanceUiAction.EmitToast(UiText.StringResource(R.string.nothing_to_update)))
                    }

                    return
                }

                state = state.copy(
                    isMakingApiCall = true
                )
                updateLeaveBalance()
            }
        }
    }

    private suspend fun getDepartmentTeacher() = viewModelScope.launch(Dispatchers.IO) {
        val cookie = ds.readCookie().first()

        val result = client.get<GetDepartmentTeacher>(
            route = EndPoints.GetDepartmentTeachers.route,
            params = listOf(
                "department" to state.department.selected
            ),
            cookieManager = cookieManager,
            gson = gson,
            ds = ds,
            cookie = cookie
        )

        if (result is Result.Success) {
            state = state.copy(
                teacher = ListHolder(
                    all = result.data.teacherName.map {
                        it.name
                    }
                ),
                isRequestingDepartment = true,
                responseTeacher = result.data,
            )
        }
    }

    private fun getLeaveBalance() = viewModelScope.launch(Dispatchers.IO) {
        val teacher = state.responseTeacher.teacherName.first {
            it.name == state.teacher.selected
        }

        val result = client.get<List<TeacherLeaveBalance>>(
            route = EndPoints.GetTeacherLeaveBalance.route,
            params = listOf(
                "teacherId" to teacher.id.toString(),
            ),
            cookieManager = cookieManager,
            gson = gson,
            ds = ds,
            cookie = ds.readCookie().first()
        )

        if (result is Result.Success) {
            state = state.copy(
                responseBalance = result.data,
                mapOfLeave = result.data.associate {
                    it.name to it.balance
                },
                listOfLeave = ListHolder(
                    all = result.data.map {
                        it.name
                    }
                ),
                isRequestingTeacher = true
            )
        }
    }

    private fun updateLeaveBalance() {
        viewModelScope.launch {
            val balance = state.responseBalance.first {
                it.name == state.listOfLeave.selected
            }

            val teacher = state.responseTeacher.teacherName.first {
                it.name == state.teacher.selected
            }


            val result = client.post<UpdateLeaveBalanceReq, Boolean>(
                route = EndPoints.UpdateTeacherLeaveBalance.route,
                body = UpdateLeaveBalanceReq(
                    teacherId = teacher.id,
                    leaveId = balance.id,
                    value = state.leaveBalance
                ),
                gson = gson,
                cookie = ds.readCookie().first(),
                cookieManager = cookieManager,
                ds = ds
            )

            when (result) {
                is Result.Error -> {
                    when (result.error) {
                        DataError.Network.NO_INTERNET -> {
                            _uiEvent.send(
                                UpdateBalanceUiAction.EmitToast(
                                    UiText.StringResource(R.string.error_internet)
                                )
                            )
                        }

                        else -> {
                            _uiEvent.send(
                                UpdateBalanceUiAction.EmitToast(
                                    UiText.StringResource(R.string.error_something_went_wrong)
                                )
                            )
                        }
                    }
                }

                is Result.Success -> {
                    state = state.copy(
                        responseBalance = state.responseBalance.map {
                            if (it.name == state.listOfLeave.selected) {
                                it.copy(balance = state.leaveBalance)
                            } else it
                        },
                        isMakingApiCall = false
                    )

                    _uiEvent.send(
                        UpdateBalanceUiAction.EmitToast(
                            UiText.StringResource(R.string.balance_updated)
                        )
                    )
                }
            }
        }
    }
}
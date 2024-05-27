package com.poulastaa.lms.presentation.apply_leave

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.domain.repository.utils.ConnectivityObserver
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyLeaveViewModel @Inject constructor(
    private val network: ConnectivityObserver,
    private val ds: DataStoreRepository
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
                        )
                    )
                }

                UserType.SACT -> {
                    state = state.copy(
                        leaveType = state.leaveType.copy(
                            all = listOf(
                                "Casual Leave",
                                "Maternity Leave",
                                "Medical Leave"
                            )
                        )
                    )
                }

                else -> Unit
            }
        }
    }

    fun onEvent(event: ApplyLeaveUiEvent) {
        when (event) {
            is ApplyLeaveUiEvent.OnLeaveTypeSelected -> {
                state = state.copy(
                    leaveType = state.leaveType.copy(
                        isDialogOpen = !state.leaveType.isDialogOpen,
                        selected = state.leaveType.all[event.index]
                    )
                )
            }

            is ApplyLeaveUiEvent.OnDayTypeSelected -> {
                state = state.copy(
                    dayType = state.dayType.copy(
                        isDialogOpen = !state.dayType.isDialogOpen,
                        selected = state.dayType.all[event.index]
                    )
                )
            }

            is ApplyLeaveUiEvent.OnFromDateSelect -> {

            }

            is ApplyLeaveUiEvent.OnToDateSelect -> {

            }

            is ApplyLeaveUiEvent.OnLeaveReason -> {

            }

            is ApplyLeaveUiEvent.OnAddressDuringLeaveSelected -> {
                state = state.copy(
                    addressDuringLeave = state.addressDuringLeave.copy(
                        isDialogOpen = !state.addressDuringLeave.isDialogOpen,
                        selected = state.addressDuringLeave.all[event.index]
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

            ApplyLeaveUiEvent.OnFromDateToggle -> {

            }

            ApplyLeaveUiEvent.OnToDateToggle -> {

            }
        }
    }
}
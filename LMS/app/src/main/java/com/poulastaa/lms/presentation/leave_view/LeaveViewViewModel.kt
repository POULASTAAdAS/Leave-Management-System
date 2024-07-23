package com.poulastaa.lms.presentation.leave_view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.poulastaa.lms.data.model.leave.ViewLeaveSingleRes
import com.poulastaa.lms.data.remote.ViewLeavePagingSource
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveViewViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val pagingSource: ViewLeavePagingSource,
) : ViewModel() {
    var state by mutableStateOf(LeaveViewUiState())

    private val _uiEvent = Channel<LeaveViewUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _leave: MutableStateFlow<PagingData<ViewLeaveSingleData>> =
        MutableStateFlow(PagingData.empty())
    var leave = _leave.asStateFlow()
        private set

    private var loadLeaveJob: Job? = null

    init {
        viewModelScope.launch {
            val user = ds.readUser().first()

            val isHead = user.isDepartmentInCharge
            val department = user.department

            if (!isHead) {
                state = state.copy(
                    department = state.department.copy(
                        all = state.department.all + listOf(
                            "ASP(Advertisement and Sales Promotion)",
                            "Bengali",
                            "Botany",
                            "Chemistry",
                            "Commerce",
                            "Computer Science",
                            "Economics",
                            "Education",
                            "Electronic Science",
                            "English",
                            "Environmental Science",
                            "Food & Nutrition",
                            "Geography",
                            "Hindi",
                            "History",
                            "Journalism & Mass Com.",
                            "Mathematics",
                            "Philosophy",
                            "Physical Education",
                            "Physics",
                            "Physiology",
                            "Sanskrit",
                            "Sociology",
                            "Urdu",
                            "Zoology",
                            "Other"
                        )
                    )
                )
            } else {
                state = state.copy(
                    teacherDepartment = department
                )
            }

            loadLeaveJob?.cancel()
            loadLeaveJob = loadLeave()
        }
    }

    private fun loadLeave() = viewModelScope.launch {
        _leave.value = PagingData.empty()
        pagingSource.setDepartment(state.department.selected)

        Log.d("called", "called")

        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            initialKey = 1,
        ) {
            pagingSource
        }.flow.map {
            it.map { entry ->
                entry.toViewLeaveSingleData()
            }
        }.cachedIn(viewModelScope)
            .collectLatest {
                _leave.value = it
            }
    }

    fun onEvent(event: LeaveViewUiEvent) {
        when (event) {
            LeaveViewUiEvent.OnDepartmentToggle -> {
                state = state.copy(
                    department = state.department.copy(
                        isDialogOpen = !state.department.isDialogOpen
                    )
                )
            }

            is LeaveViewUiEvent.OnLeaveToggle -> {
                _leave.value = _leave.value.map {
                    if (it.department == event.department) it.copy(
                        isExpanded = it.isExpanded.not()
                    )
                    else it
                }
            }

            is LeaveViewUiEvent.OnDepartmentChange -> {
                val newDepartment = state.department.all[event.index]
                val oldDepartment = state.department.selected

                state = state.copy(
                    department = state.department.copy(
                        selected = newDepartment,
                        isDialogOpen = false
                    )
                )

                if (oldDepartment == newDepartment) return

                loadLeaveJob?.cancel()
                loadLeaveJob = loadLeave()
            }
        }
    }

    private fun ViewLeaveSingleRes.toViewLeaveSingleData(isExpanded: Boolean = true): ViewLeaveSingleData {
        return ViewLeaveSingleData(
            isExpanded = isExpanded,
            department = this.department,
            listOfLeave = this.listOfLeave.map {
                ViewLeaveInfo(
                    reqData = it.reqData,
                    name = it.name,
                    leaveType = it.leaveType,
                    fromDate = it.fromDate,
                    toDate = it.toDate,
                    totalDays = it.totalDays,
                    status = it.status,
                    cause = it.cause
                )
            }
        )
    }
}
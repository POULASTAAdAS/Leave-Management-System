package com.poulastaa.lms.presentation.leave_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.poulastaa.lms.data.model.leave.ViewLeaveSingleRes
import com.poulastaa.lms.data.remote.ViewLeavePagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveViewViewModel @Inject constructor(
    private val pagingSource: ViewLeavePagingSource,
) : ViewModel() {
    private val _uiEvent = Channel<LeaveViewUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _leave: MutableStateFlow<PagingData<ViewLeaveSingleData>> =
        MutableStateFlow(PagingData.empty())
    var leave = _leave.asStateFlow()
        private set

    init {

    }

    fun loadLeave() { // handling pager is fucking pain
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false
                ),
                initialKey = 1
            ) {
                pagingSource
            }.flow.map {
                it.map { it.toViewLeaveSingleData() }
            }.cachedIn(viewModelScope)
                .collectLatest {
                    _leave.value = it
                }
        }
    }

    fun onEvent(event: LeaveViewUiEvent) {
        when (event) {
            is LeaveViewUiEvent.DepartmentToggle -> {
                _leave.value = _leave.value.map {
                    if (it.department == event.department) it.copy(
                        isExpanded = it.isExpanded.not()
                    ) else it
                }
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
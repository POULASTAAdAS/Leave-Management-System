package com.poulastaa.lms.presentation.leave_history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.poulastaa.lms.data.remote.LeaveHistoryPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveHistoryViewModel @Inject constructor(
    private val pagingSource: LeaveHistoryPagingSource
) : ViewModel() {
    var state by mutableStateOf(LeaveHistoryUiState())
        private set

    private val _uiEvent = Channel<LeaveHistoryUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _leave: MutableStateFlow<PagingData<LeaveInfo>> =
        MutableStateFlow(PagingData.empty())

    var leave = _leave.asStateFlow()
        private set

    fun loadLeave() {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false
                ),
                initialKey = 1
            ) {
                pagingSource
            }.flow.cachedIn(viewModelScope).collectLatest {
                _leave.value = it
            }
        }
    }
}
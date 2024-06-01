package com.poulastaa.lms.presentation.leave_history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

class LeaveHistoryViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val gson: Gson,
    private val cookieManager: CookieManager,
    private val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(LeaveHistoryUiState())
        private set

    private val _uiEvent = Channel<LeaveHistoryUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()
}
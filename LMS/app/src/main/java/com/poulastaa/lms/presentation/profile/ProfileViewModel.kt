package com.poulastaa.lms.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.data.repository.utils.NetworkConnectivityObserver
import com.poulastaa.lms.domain.repository.utils.ConnectivityObserver
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val network: NetworkConnectivityObserver,
    private val ds: DataStoreRepository,
    private val gson: Gson,
    private val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(ProfileUiState())
        private set

    init {
        viewModelScope.launch {
            network.observe().collectLatest {
                state = if (it == ConnectivityObserver.NetworkStatus.AVAILABLE) state.copy(
                    isInternet = true
                ) else state.copy(
                    isInternet = false
                )
            }
        }
    }

    private var getDataJob: Job? = null

    fun startJob() {
        getDataJob?.cancel()
        getDataJob = getDataJob()
    }

    fun cancelJob() = getDataJob?.cancel()

    fun onEvent(event: ProfileUiEvent) {

    }

    private fun getDataJob() = viewModelScope.launch(Dispatchers.IO) {
        val cookie = ds.readCookie().first()

//        val response = client.get<>()
    }
}
package com.poulastaa.lms.presentation.home.type

import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeScreenTypeViewModel @Inject constructor(
    private val ds: DataStoreRepository,
) : ViewModel() {
    var state by mutableStateOf(HomeScreenTypeUiState())
        private set


    private val _uiEvent = Channel<HomeScreenTypeUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            ds.readUser().collectLatest {
                state = state.copy(
                    user = it
                )

                Log.d("user", it.toString())
            }
        }

        viewModelScope.launch {
            ds.readCookie().collectLatest {
                state = state.copy(
                    cookie = it
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val localTime = LocalDateTime.now().toLocalTime()
                val currentTime = localTime.format(DateTimeFormatter.ofPattern("hh")).toInt()
                val status = localTime.format(DateTimeFormatter.ofPattern("a"))

                if (status.uppercase() == "AM") {
                    state = if (currentTime == 12) {
                        state.copy(
                            time = "Mid Night"
                        )
                    } else if (currentTime >= 4) {
                        state.copy(
                            time = "Good Morning"
                        )
                    } else {
                        state.copy(
                            time = "Night Owl"
                        )
                    }
                } else {
                    state = if (currentTime <= 5 || currentTime == 12) {
                        state.copy(
                            time = "Good Afternoon"
                        )
                    } else if (currentTime in 6..10) {
                        state.copy(
                            time = "Good Evening"
                        )
                    } else if (currentTime in 10..11) {
                        state.copy(
                            time = "Good Night"
                        )
                    } else {
                        state.copy(
                            time = "Night Owl"
                        )
                    }
                }
            } else {
                state = state.copy(
                    time = "Hello ${state.user.name}"
                )
            }
        }
    }
}
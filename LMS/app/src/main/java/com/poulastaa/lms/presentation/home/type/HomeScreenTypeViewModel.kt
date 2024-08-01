package com.poulastaa.lms.presentation.home.type

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
}
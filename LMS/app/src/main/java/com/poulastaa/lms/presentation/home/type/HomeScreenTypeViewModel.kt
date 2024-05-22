package com.poulastaa.lms.presentation.home.type

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenTypeViewModel @Inject constructor(
    private val ds: DataStoreRepository
) : ViewModel() {
    var userType by mutableStateOf(UserType.NON)
        private set


    private val _uiEvent = Channel<HomeScreenTypeUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val user = ds.readUser()
            val cookie = ds.readCookie().first()

            Log.d("user", "user: $user , cookie: $cookie")

            userType = when (user.userType) {
                UserType.PRINCIPLE -> UserType.PRINCIPLE

                UserType.PERMANENT -> UserType.PERMANENT

                UserType.SACT -> UserType.SACT

                UserType.NON -> {
                    viewModelScope.launch(Dispatchers.IO) {
//                        storeSignInState(Screens.Auth, ds)

                        _uiEvent.send(HomeScreenTypeUiAction.Err)
                    }

                    return@launch
                }
            }
        }
    }
}
package com.poulastaa.lms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStoreRepository
) : ViewModel() {
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    private val _keepSplashOn = MutableStateFlow(true)
    val keepSplashOn get() = _keepSplashOn.asStateFlow()

    init {
        viewModelScope.launch {
            val value = dataStore.readSignInState().first()

            when (value) {
                Screens.Auth -> _startDestination.value = Screens.Auth.route

                Screens.Home -> _startDestination.value = Screens.Home.route

                Screens.StoreDetails -> _startDestination.value = Screens.StoreDetails.route
            }.let {
                _keepSplashOn.value = false
            }
        }
    }
}
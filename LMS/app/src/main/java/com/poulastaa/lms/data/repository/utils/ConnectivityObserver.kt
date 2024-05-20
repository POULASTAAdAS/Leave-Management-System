package com.poulastaa.lms.data.repository.utils

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<NetworkStatus>

    enum class NetworkStatus {
        AVAILABLE,
        UNAVAILABLE,
        LOST
    }
}
package com.poulastaa.lms.domain.repository.utils

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<NetworkStatus>

    enum class NetworkStatus {
        AVAILABLE,
        UNAVAILABLE,
        LOST
    }
}
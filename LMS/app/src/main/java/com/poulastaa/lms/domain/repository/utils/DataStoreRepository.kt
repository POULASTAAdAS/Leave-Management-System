package com.poulastaa.lms.domain.repository.utils

import com.poulastaa.lms.data.model.auth.LocalUser
import com.poulastaa.lms.navigation.Screens
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun storeSignInState(state: Screens)
    fun readSignInState(): Flow<Screens>

    suspend fun storeCookie(data: String)
    fun readCookie(): Flow<String>

    suspend fun storeLocalUser(user: LocalUser)
    suspend fun readUser(): Flow<LocalUser>

    suspend fun clearAll()
}
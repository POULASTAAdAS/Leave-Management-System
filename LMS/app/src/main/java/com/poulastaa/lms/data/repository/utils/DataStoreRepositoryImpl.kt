package com.poulastaa.lms.data.repository.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.poulastaa.lms.data.model.auth.LocalUser
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.navigation.Screens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {
    private object PreferencesKeys {
        val SIGN_IN_STATE = stringPreferencesKey(name = "sign_in_state")
        val COOKIE = stringPreferencesKey(name = "cookie")
        val USER = stringPreferencesKey(name = "user")
    }

    override suspend fun storeSignInState(state: Screens) {
        when (state) {
            Screens.Auth -> Screens.Auth.route
            Screens.Home -> Screens.Home.route
            Screens.StoreDetails -> Screens.StoreDetails.route
        }.let { value ->
            dataStore.edit {
                it[PreferencesKeys.SIGN_IN_STATE] = value
            }
        }
    }

    override fun readSignInState(): Flow<Screens> = dataStore.data.catch {
        emit(emptyPreferences())
    }.map {
        val state = it[PreferencesKeys.SIGN_IN_STATE] ?: Screens.Auth.route

        when (state) {
            Screens.Auth.route -> Screens.Auth
            Screens.Home.route -> Screens.Home
            else -> Screens.StoreDetails
        }
    }

    override suspend fun storeCookie(data: String) {
        dataStore.edit {
            it[PreferencesKeys.COOKIE] = data
        }
    }

    override fun readCookie(): Flow<String> = dataStore.data.catch {
        emit(emptyPreferences())
    }.map {
        it[PreferencesKeys.COOKIE] ?: ""
    }

    override suspend fun storeLocalUser(user: LocalUser) {
        val json = Gson().toJson(user)

        dataStore.edit {
            it[PreferencesKeys.USER] = json
        }
    }

    override suspend fun readUser(): LocalUser {
        val preferences = dataStore.data.catch {
            emit(emptyPreferences())
        }.first()

        val response = preferences[PreferencesKeys.USER]?.let {
            val user = Gson().fromJson(it, LocalUser::class.java)

            user
        } ?: LocalUser()

        return response
    }
}
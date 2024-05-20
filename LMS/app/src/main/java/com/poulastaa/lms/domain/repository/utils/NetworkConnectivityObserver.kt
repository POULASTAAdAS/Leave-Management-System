package com.poulastaa.lms.domain.repository.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.poulastaa.lms.data.repository.utils.ConnectivityObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@ViewModelScoped
class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext val context: Context
) : ConnectivityObserver {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observe(): Flow<ConnectivityObserver.NetworkStatus> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    val networkStatus = ConnectivityObserver.NetworkStatus.AVAILABLE
                    trySend(networkStatus)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    val networkStatus = ConnectivityObserver.NetworkStatus.LOST
                    trySend(networkStatus)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    val networkStatus = ConnectivityObserver.NetworkStatus.UNAVAILABLE
                    trySend(networkStatus)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}
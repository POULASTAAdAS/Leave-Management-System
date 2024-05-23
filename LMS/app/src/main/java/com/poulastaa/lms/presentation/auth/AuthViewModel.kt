package com.poulastaa.lms.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.AuthReq
import com.poulastaa.lms.data.model.auth.AuthRes
import com.poulastaa.lms.data.model.auth.AuthStatus
import com.poulastaa.lms.data.model.auth.EmailVerificationRes
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.auth.LocalUser
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.data.remote.authGet
import com.poulastaa.lms.data.remote.authPost
import com.poulastaa.lms.data.remote.extractCookie
import com.poulastaa.lms.domain.repository.auth.PatternValidator
import com.poulastaa.lms.domain.repository.utils.ConnectivityObserver
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.UiText
import com.poulastaa.lms.ui.utils.storeCookie
import com.poulastaa.lms.ui.utils.storeSignInState
import com.poulastaa.lms.ui.utils.storeUser
import com.poulastaa.lms.ui.utils.toLocalUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val network: ConnectivityObserver,
    private val cookieManager: CookieManager,
    private val ds: DataStoreRepository,
    private val gson: Gson,
    private val emailValidator: PatternValidator,
    private val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(AuthUiState())
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

    private val _uiEvent = Channel<AuthUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var emailVerificationJob: Job? = null
    private var resendVerificationMailJob: Job? = null

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.EmailChanged -> {
                state = state.copy(
                    email = event.value,
                    isEmailErr = false,
                    emailErr = UiText.DynamicString(""),
                    isValidEmail = emailValidator.matches(event.value),
                    isEmailHintVisible = false
                )
            }

            AuthUiEvent.OnContinueClick -> {
                if (!state.isInternet) {

                    viewModelScope.launch(Dispatchers.IO) {
                        _uiEvent.send(
                            AuthUiAction.SendToast(
                                UiText.StringResource(R.string.error_internet)
                            )
                        )
                    }

                    return
                }

                if (!state.isValidEmail) {
                    state = state.copy(
                        isEmailErr = true,
                        emailErr = UiText.StringResource(R.string.error_email_not_valid)
                    )

                    return
                }


                if (state.isMakingApiCall) {
                    viewModelScope.launch(Dispatchers.IO) {
                        _uiEvent.send(
                            AuthUiAction.SendToast(
                                UiText.StringResource(R.string.error_making_api_call)
                            )
                        )
                    }

                    return
                }


                state = state.copy(
                    isMakingApiCall = true
                )

                authenticate(state.email)
            }

            AuthUiEvent.OnResendVerificationClick -> {
                emailVerificationJob?.cancel()
                resendVerificationMailJob?.cancel()

                state = state.copy(
                    resendVerificationEmailButtonEnabled = false
                )

                emailVerificationJob = emailVerificationCheck(
                    email = state.email,
                    route = state.route,
                    isLogIn = true
                )
                resendVerificationMailJob = resendVerificationMail()
            }
        }
    }

    private fun authenticate(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = client.authPost<AuthReq, AuthRes>(
                route = EndPoints.Auth.route,
                body = AuthReq(
                    email = email
                ),
                gson = gson
            )

            when (response) {
                is Result.Error -> {
                    when (response.error) {
                        DataError.Network.NOT_FOUND -> {
                            state = state.copy(
                                isEmailErr = true,
                                emailErr = UiText.StringResource(R.string.error_email_not_found)
                            )
                        }

                        else -> {
                            _uiEvent.send(
                                AuthUiAction.SendToast(
                                    UiText.StringResource(R.string.error_something_went_wrong)
                                )
                            )
                        }
                    }
                }

                is Result.Success -> {
                    _uiEvent.send(
                        AuthUiAction.SendToast(
                            UiText.StringResource(R.string.auth_mail_sent)
                        )
                    )

                    emailVerificationJob?.cancel()
                    resendVerificationMailJob?.cancel()

                    viewModelScope.launch {
                        delay(5000L)
                        resendVerificationMailJob = resendVerificationMail()

                        state = state.copy(
                            resendVerificationEmailVisible = true
                        )
                    }

                    when (response.data.authStatus) {
                        AuthStatus.PRINCIPLE_FOUND -> {
                            emailVerificationJob = emailVerificationCheck(
                                email,
                                EndPoints.LogInEmailVerificationCheck.route,
                                isLogIn = true,
                                user = response.data.user.let {
                                    LocalUser(
                                        name = it.name,
                                        email = it.email,
                                        profilePicUrl = it.profilePicUrl,
                                        userType = UserType.PRINCIPLE
                                    )
                                }
                            )

                            state = state.copy(
                                route = EndPoints.LogInEmailVerificationCheck.route
                            )
                        }

                        AuthStatus.LOGIN -> {
                            emailVerificationJob = emailVerificationCheck(
                                email,
                                EndPoints.LogInEmailVerificationCheck.route,
                                isLogIn = true,
                                user = response.data.user.toLocalUser()
                            )

                            state = state.copy(
                                route = EndPoints.LogInEmailVerificationCheck.route
                            )
                        }

                        AuthStatus.SIGNUP -> {
                            emailVerificationJob = emailVerificationCheck(
                                email,
                                EndPoints.SignUpEmailVerificationCheck.route,
                                isLogIn = false
                            )

                            state = state.copy(
                                route = EndPoints.SignUpEmailVerificationCheck.route
                            )
                        }

                        else -> null
                    }?.let {
                        viewModelScope.launch {
                            delay(5000L)
                            resendVerificationMailJob = resendVerificationMail()
                        }
                    }
                }
            }

            state = state.copy(
                isMakingApiCall = false
            )
        }
    }

    private fun emailVerificationCheck(
        email: String,
        route: String,
        isLogIn: Boolean,
        user: LocalUser = LocalUser()
    ) = viewModelScope.launch {
        for (i in 1..70) {
            delay(3000L)

            val response = client.authGet<EmailVerificationRes>(
                route = route,
                gson = gson,
                params = listOf(
                    "email" to email
                )
            )

            when (response) {
                is Result.Error -> {
                    when (response.error) {
                        DataError.Network.NO_INTERNET -> {
                            _uiEvent.send(
                                AuthUiAction.SendToast(
                                    value = UiText.StringResource(R.string.error_internet)
                                )
                            )

                            emailVerificationJob?.cancel()
                        }

                        else -> Unit
                    }
                }

                is Result.Success -> {
                    if (response.data.status) {
                        if (isLogIn) {
                            val cookie = extractCookie(cookieManager)?.let { storeCookie(it, ds) }

                            if (cookie == null) {
                                _uiEvent.send(
                                    AuthUiAction.SendToast(
                                        UiText.StringResource(R.string.error_something_went_wrong)
                                    )
                                )

                                emailVerificationJob?.cancel()
                                resendVerificationMailJob?.cancel()

                                return@launch
                            }

                            storeSignInState(Screens.Home, ds)
                            storeUser(ds, user)

                            _uiEvent.send(
                                AuthUiAction.OnSuccess(
                                    route = Screens.Home
                                )
                            )
                        } else {
                            storeSignInState(Screens.StoreDetails, ds)

                            _uiEvent.send(
                                AuthUiAction.OnSuccess(
                                    route = Screens.StoreDetails
                                )
                            )
                        }

                        emailVerificationJob?.cancel()
                        resendVerificationMailJob?.cancel()
                    }
                }
            }
        }
    }

    private fun resendVerificationMail() = viewModelScope.launch(Dispatchers.IO) {
        state = state.copy(
            resendVerificationEmailVisible = true
        )

        for (i in 20 downTo 0) {
            state = state.copy(
                resendVerificationEmailButtonText = "$i s"
            )

            delay(1000L)
        }

        state = state.copy(
            resendVerificationEmailButtonEnabled = true,
            resendVerificationEmailButtonText = "S E N D"
        )
    }
}
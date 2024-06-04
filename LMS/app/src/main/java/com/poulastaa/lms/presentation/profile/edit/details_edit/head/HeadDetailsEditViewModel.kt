package com.poulastaa.lms.presentation.profile.edit.details_edit.head

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.update_details.UpdateHeadDetailsReq
import com.poulastaa.lms.data.remote.post
import com.poulastaa.lms.data.repository.auth.UserDataValidator
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.presentation.profile.edit.details_edit.DetailsEditUiAction
import com.poulastaa.lms.ui.utils.UiText
import com.poulastaa.lms.ui.utils.storeUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

@HiltViewModel
class HeadDetailsEditViewModel @Inject constructor(
    private val validator: UserDataValidator,
    private val ds: DataStoreRepository,
    private val gson: Gson,
    private val cookieManager: CookieManager,
    private val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(HeadDetailsEditUiState())

    init {
        viewModelScope.launch {
            val user = ds.readUser().first()

            state = state.copy(
                name = state.name.copy(
                    data = user.name
                ),
                email = state.email.copy(
                    data = user.email
                )
            )
        }
    }

    private val _uiEvent = Channel<DetailsEditUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: HeadDetailsEditUiEvent) {
        when (event) {
            is HeadDetailsEditUiEvent.OnEmailChanged -> {
                state = state.copy(
                    email = state.email.copy(
                        data = event.email,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is HeadDetailsEditUiEvent.OnNameChange -> {
                state = state.copy(
                    name = state.name.copy(
                        data = event.name,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            HeadDetailsEditUiEvent.OnSaveClick -> {
                if (isErr()) return

                viewModelScope.launch(Dispatchers.IO) {
                    val userType = ds.readUser().first()
                    val cookie = ds.readCookie().first()

                    val response = client.post<UpdateHeadDetailsReq, Boolean>(
                        route = EndPoints.UpdateHeadDetails.route,
                        body = UpdateHeadDetailsReq(
                            type = userType.userType.name,
                            name = state.name.data,
                            email = state.email.data
                        ),
                        cookieManager = cookieManager,
                        gson = gson,
                        cookie = cookie,
                        ds = ds
                    )

                    when (response) {
                        is Result.Error -> {
                            when (response.error) {
                                DataError.Network.NO_INTERNET -> {
                                    _uiEvent.send(
                                        DetailsEditUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_internet)
                                        )
                                    )
                                }

                                else -> {
                                    _uiEvent.send(
                                        DetailsEditUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_something_went_wrong)
                                        )
                                    )
                                }
                            }
                        }

                        is Result.Success -> {
                            if (response.data) {
                                _uiEvent.send(
                                    DetailsEditUiAction.ShowToast(
                                        UiText.StringResource(R.string.success_details_updated)
                                    )
                                )
                            }

                            val user = ds.readUser().first()

                            storeUser(
                                ds = ds,
                                localUser = user.copy(
                                    name = state.name.data,
                                    email = state.email.data
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun isErr(): Boolean {
        var err = false

        if (!validator.isValidEmail(state.email.data)) {
            state = state.copy(
                name = state.name.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_email_not_valid)
                )
            )

            err = true
        }

        when (validator.isValidUserName(state.name.data)) {
            UserDataValidator.UserNameState.CAN_NOT_CONTAIN_UNDERSCORE -> {
                state = state.copy(
                    name = state.name.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_cant_contain_underscore)
                    )
                )
            }

            UserDataValidator.UserNameState.CANT_BE_EMPTY -> {
                state = state.copy(
                    name = state.name.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            }

            UserDataValidator.UserNameState.VALID -> Unit
        }


        if (err) {
            viewModelScope.launch {
                _uiEvent.send(
                    DetailsEditUiAction.ShowToast(
                        UiText.StringResource(R.string.error_field_are_empty_or_invalid)
                    )
                )
            }
        }


        return err
    }
}
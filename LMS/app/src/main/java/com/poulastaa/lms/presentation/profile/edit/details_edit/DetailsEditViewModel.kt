package com.poulastaa.lms.presentation.profile.edit.details_edit

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.update_details.UpdateDetailsReq
import com.poulastaa.lms.data.remote.post
import com.poulastaa.lms.data.repository.auth.UserDataValidator
import com.poulastaa.lms.data.repository.utils.NetworkConnectivityObserver
import com.poulastaa.lms.domain.repository.utils.ConnectivityObserver
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.ui.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject


@HiltViewModel
class DetailsEditViewModel @Inject constructor(
    private val network: NetworkConnectivityObserver,
    private val validator: UserDataValidator,
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(DetailsEditUiState())

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

    fun populate(
        name: String,
        email: String,
        phoneOne: String,
        phoneTwo: String,
        qualification: String
    ) {
        Log.d("phoneTwo", phoneTwo)

        state = state.copy(
            name = state.name.copy(
                data = name
            ),
            email = state.email.copy(
                data = email
            ),
            phoneOne = state.phoneOne.copy(
                data = phoneOne
            ),
            phoneTwo = state.phoneTwo.copy(
                data = if (phoneTwo == "0") "" else phoneTwo
            ),
            qualification = state.qualification.copy(
                selected = state.qualification.all[state.qualification.all.indexOf(qualification)]
            )
        )
    }

    private val _uiEvent = Channel<DetailsEditUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: DetailsEditUiEvent) {
        when (event) {
            is DetailsEditUiEvent.OnNameChange -> {
                state = state.copy(
                    name = state.name.copy(
                        data = event.name,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is DetailsEditUiEvent.OnEmailChanged -> {
                state = state.copy(
                    email = state.email.copy(
                        data = event.email,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is DetailsEditUiEvent.OnPhoneNumberOneChange -> {
                state = state.copy(
                    phoneOne = state.phoneOne.copy(
                        data = event.phoneNumber,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is DetailsEditUiEvent.OnPhoneNumberTwoChange -> {
                state = state.copy(
                    phoneTwo = state.phoneTwo.copy(
                        data = event.phoneNumber,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }


            DetailsEditUiEvent.OnQualificationDropDownClick -> {
                state = state.copy(
                    qualification = state.qualification.copy(
                        isDialogOpen = !state.qualification.isDialogOpen
                    )
                )
            }

            is DetailsEditUiEvent.OnQualificationSelected -> {
                state = state.copy(
                    qualification = state.qualification.copy(
                        isDialogOpen = false,
                        selected = state.qualification.all[event.index]

                    )
                )
            }

            DetailsEditUiEvent.OnSaveClick -> {
                if (isErr()) return

                if (state.isMakingApiCall) return

                state = state.copy(
                    isMakingApiCall = true
                )

                viewModelScope.launch(Dispatchers.IO) {
                    val cookie = ds.readCookie().first()

                    val response = client.post<UpdateDetailsReq, Boolean>(
                        route = EndPoints.UpdateDetails.route,
                        body = UpdateDetailsReq(
                            name = state.name.data,
                            email = state.email.data,
                            phoneOne = state.phoneOne.data,
                            phoneTwo = state.phoneTwo.data,
                            qualification = state.qualification.selected
                        ),
                        gson = gson,
                        cookie = cookie,
                        cookieManager = cookieManager,
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
                                val email = state.email.data
                                val name = state.name.data
                                val phoneOne = state.phoneOne.data

                                val user = ds.readUser().first()

                                val newEmail = if (user.email != email && email.isNotEmpty()) email
                                else user.email
                                val newName =
                                    if (user.name != name && name.isNotEmpty()) name else user.name
                                val newPhoneOne =
                                    if (user.phone != phoneOne && phoneOne.isNotEmpty()) phoneOne
                                    else user.phone

                                ds.storeLocalUser(
                                    user = user.copy(
                                        email = newEmail,
                                        name = newName,
                                        phone = newPhoneOne
                                    )
                                )

                                _uiEvent.send(
                                    DetailsEditUiAction.ShowToast(
                                        UiText.StringResource(R.string.success_details_updated)
                                    )
                                )
                            } else {
                                _uiEvent.send(
                                    DetailsEditUiAction.ShowToast(
                                        UiText.StringResource(R.string.error_something_went_wrong)
                                    )
                                )
                            }
                        }
                    }

                    state = state.copy(
                        isMakingApiCall = false
                    )
                }
            }

            DetailsEditUiEvent.SomethingWentWrong -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiEvent.send(
                        DetailsEditUiAction.ShowToast(
                            UiText.StringResource(R.string.error_something_went_wrong)
                        )
                    )
                }
            }
        }
    }

    private fun isErr(): Boolean {
        var isErr = false

        if (!validator.isValidEmail(state.email.data.trim())) {
            state = state.copy(
                email = state.email.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_email_not_valid)
                )
            )

            isErr = true
        }
        when (validator.isValidUserName(state.name.data)) {
            UserDataValidator.UserNameState.CAN_NOT_CONTAIN_UNDERSCORE -> {
                state = state.copy(
                    name = state.name.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.err_cant_contain_blank_sapce)
                    )
                )

                isErr = true
            }

            UserDataValidator.UserNameState.CANT_BE_EMPTY -> {
                state = state.copy(
                    name = state.name.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )

                isErr = true
            }

            UserDataValidator.UserNameState.VALID -> Unit
        }

        if (!validator.isValidPhoneNumber(state.phoneOne.data)) {
            state = state.copy(
                phoneOne = state.phoneOne.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_phone_number_not_valid)
                )
            )

            isErr = true
        }

        if (state.phoneTwo.data.isNotEmpty()) {
            if (!validator.isValidPhoneNumber(state.phoneTwo.data)) {
                state = state.copy(
                    phoneTwo = state.phoneTwo.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_phone_number_not_valid)
                    )
                )

                isErr = true
            }
        }


        if (isErr) {
            viewModelScope.launch(Dispatchers.IO) {
                _uiEvent.send(
                    DetailsEditUiAction.ShowToast(
                        UiText.StringResource(R.string.error_field_are_empty_or_invalid)
                    )
                )
            }
        }

        return isErr
    }
}

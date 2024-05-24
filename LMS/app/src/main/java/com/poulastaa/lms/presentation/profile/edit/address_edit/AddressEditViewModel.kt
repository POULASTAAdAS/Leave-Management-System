package com.poulastaa.lms.presentation.profile.edit.address_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.stoe_details.AddressType
import com.poulastaa.lms.data.model.stoe_details.UpdateAddressReq
import com.poulastaa.lms.data.remote.post
import com.poulastaa.lms.data.repository.auth.UserDataValidator
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
class AddressEditViewModel @Inject constructor(
    private val validator: UserDataValidator,
    private val network: ConnectivityObserver,
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(AddressEditUiState())
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

    private val _uiEvent = Channel<AddressEditUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun populate(
        type: String,
        houseNo: String,
        street: String,
        city: String,
        state: String,
        zipCode: String
    ) {
        this.state = this.state.copy(
            type = AddressType.valueOf(type),
            houseNumber = this.state.houseNumber.copy(
                data = houseNo.replace(
                    oldChar = '_',
                    newChar = '/'
                )
            ),
            street = this.state.street.copy(
                data = street
            ),
            city = this.state.city.copy(
                data = city
            ),
            pinCode = this.state.pinCode.copy(
                data = zipCode
            ),
            state = this.state.state.copy(
                data = state
            )
        )
    }

    fun onEvent(event: AddressEditUiEvent) {
        when (event) {
            is AddressEditUiEvent.OnHouseNumberChang -> {
                state = state.copy(
                    houseNumber = state.houseNumber.copy(
                        data = event.value,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is AddressEditUiEvent.OnStreetChang -> {
                state = state.copy(
                    street = state.street.copy(
                        data = event.value,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is AddressEditUiEvent.OnCityChang -> {
                state = state.copy(
                    city = state.city.copy(
                        data = event.value,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is AddressEditUiEvent.OnPostalCodeChang -> {
                state = state.copy(
                    pinCode = state.pinCode.copy(
                        data = event.value,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is AddressEditUiEvent.OnStateChang -> {
                state = state.copy(
                    state = state.state.copy(
                        data = event.value,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            AddressEditUiEvent.OnSaveClick -> {
                if (state.isMakingApiCall || isErr()) return

                state = state.copy(
                    isMakingApiCall = true
                )

                viewModelScope.launch(Dispatchers.IO) {
                    val cookie = ds.readCookie().first()

                    val response = client.post<UpdateAddressReq, Boolean>(
                        route = EndPoints.UpdateAddress.route,
                        body = UpdateAddressReq(
                            type = state.type,
                            houseNo = state.houseNumber.data,
                            street = state.street.data,
                            city = state.city.data,
                            zipCode = state.pinCode.data,
                            state = state.state.data,
                            otherType = state.otherSelected
                        ),
                        cookieManager = cookieManager,
                        gson = gson,
                        ds = ds,
                        cookie = cookie
                    )


                    when (response) {
                        is Result.Error -> {
                            when (response.error) {
                                DataError.Network.NO_INTERNET -> {
                                    _uiEvent.send(
                                        AddressEditUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_internet)
                                        )
                                    )
                                }

                                else -> {
                                    _uiEvent.send(
                                        AddressEditUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_something_went_wrong)
                                        )
                                    )
                                }
                            }
                        }

                        is Result.Success -> {
                            if (response.data) {
                                _uiEvent.send(
                                    AddressEditUiAction.ShowToast(
                                        UiText.StringResource(R.string.address_updated)
                                    )
                                )
                            } else {
                                _uiEvent.send(
                                    AddressEditUiAction.ShowToast(
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

            AddressEditUiEvent.OtherAddressSelected -> {
                state = state.copy(
                    otherSelected = !state.otherSelected
                )
            }

            AddressEditUiEvent.SomethingWentWrong -> {
                viewModelScope.launch {
                    _uiEvent.send(
                        AddressEditUiAction.ShowToast(
                            UiText.StringResource(R.string.error_something_went_wrong)
                        )
                    )
                }
            }
        }
    }

    private fun isErr(): Boolean {
        var isErr = false

        if (!validator.isValidZip(state.pinCode.data)) {
            state = state.copy(
                pinCode = state.pinCode.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_zip_code)
                )
            )

            isErr = true
        }

        if (state.houseNumber.data.isEmpty()) {
            state = state.copy(
                houseNumber = state.houseNumber.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (state.street.data.isEmpty()) {
            state = state.copy(
                street = state.street.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (state.city.data.isEmpty()) {
            state = state.copy(
                city = state.city.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (state.pinCode.data.isEmpty()) {
            state = state.copy(
                pinCode = state.pinCode.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (state.state.data.isEmpty()) {
            state = state.copy(
                state = state.state.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (isErr) {
            viewModelScope.launch {
                _uiEvent.send(
                    AddressEditUiAction.ShowToast(
                        UiText.StringResource(R.string.error_field_are_empty_or_invalid)
                    )
                )
            }
        }

        return isErr
    }
}

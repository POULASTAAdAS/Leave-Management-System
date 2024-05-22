package com.poulastaa.lms.presentation.store_details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.auth.LocalUser
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.data.model.stoe_details.SetDetailsRes
import com.poulastaa.lms.data.model.stoe_details.StoreDetailsReq
import com.poulastaa.lms.data.model.stoe_details.TeacherDetailsSaveStatus
import com.poulastaa.lms.data.remote.authPost
import com.poulastaa.lms.data.remote.extractCookie
import com.poulastaa.lms.data.repository.auth.UserDataValidator
import com.poulastaa.lms.domain.repository.utils.ConnectivityObserver
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.DateUtils
import com.poulastaa.lms.ui.utils.UiText
import com.poulastaa.lms.ui.utils.storeCookie
import com.poulastaa.lms.ui.utils.storeSignInState
import com.poulastaa.lms.ui.utils.storeUser
import com.poulastaa.lms.ui.utils.toStoreDetailsReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

@HiltViewModel
class StoreDetailsViewModel @Inject constructor(
    private val network: ConnectivityObserver,
    private val validator: UserDataValidator,
    private val cookieManager: CookieManager,
    private val ds: DataStoreRepository,
    private val gson: Gson,
    private val client: OkHttpClient
) : ViewModel() {
    var state by mutableStateOf(StoreDetailsUiState())
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


    private val _uiEvent = Channel<StoreDetailsUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: StoreDetailsUiEvent) {
        when (event) {
            is StoreDetailsUiEvent.OnUserNameChange -> {
                state = state.copy(
                    userName = state.userName.copy(
                        data = event.userName,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is StoreDetailsUiEvent.OnHRMSIDChange -> {
                state = state.copy(
                    hrmsId = state.hrmsId.copy(
                        data = event.id,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            is StoreDetailsUiEvent.OnEmailChanged -> {
                state = state.copy(
                    email = state.email.copy(
                        data = event.email,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            // phone number
            is StoreDetailsUiEvent.OnPhoneNumberOneChange -> {
                state = state.copy(
                    phoneOne = state.phoneOne.copy(
                        data = event.phoneNumber,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }

            // phone number
            is StoreDetailsUiEvent.OnPhoneNumberTwoChange -> {
                state = state.copy(
                    phoneTwo = state.phoneTwo.copy(
                        data = event.phoneNumber,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }


            // birth date
            StoreDetailsUiEvent.OnBDateDialogClick -> {
                state = state.copy(
                    bDay = state.bDay.copy(
                        isDialogOpen = !state.bDay.isDialogOpen
                    )
                )
            }

            is StoreDetailsUiEvent.OnBDateChange -> {
                state = state.copy(
                    bDay = state.bDay.copy(
                        isDialogOpen = !state.bDay.isDialogOpen,
                        data = event.date,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    )
                )
            }


            // gender
            StoreDetailsUiEvent.OnGenderDropDownClick -> {
                state = state.copy(
                    gender = state.gender.copy(
                        isDialogOpen = !state.gender.isDialogOpen
                    )
                )
            }

            is StoreDetailsUiEvent.OnGenderSelected -> {
                state = state.copy(
                    gender = state.gender.copy(
                        isDialogOpen = false,
                        isErr = false,
                        errText = UiText.DynamicString(""),
                        selected = state.gender.all[event.index]
                    )
                )
            }

            // designation
            StoreDetailsUiEvent.OnDesignationDropDownClick -> {
                state = state.copy(
                    designation = state.designation.copy(
                        isDialogOpen = !state.designation.isDialogOpen
                    )
                )
            }

            is StoreDetailsUiEvent.OnDesignationSelected -> {
                state = state.copy(
                    designation = state.designation.copy(
                        isDialogOpen = false,
                        isErr = false,
                        errText = UiText.DynamicString(""),
                        selected = state.designation.all[event.index]
                    )
                )
            }

            // department
            StoreDetailsUiEvent.OnDepartmentDropDownClick -> {
                state = state.copy(
                    department = state.department.copy(
                        isDialogOpen = !state.department.isDialogOpen
                    )
                )
            }

            is StoreDetailsUiEvent.OnDepartmentSelected -> {
                state = state.copy(
                    department = state.department.copy(
                        isDialogOpen = false,
                        isErr = false,
                        errText = UiText.DynamicString(""),
                        selected = state.department.all[event.index]
                    )
                )
            }

            // joining date
            StoreDetailsUiEvent.OnJoinDateDialogClick -> {
                state = state.copy(
                    joiningDate = state.joiningDate.copy(
                        isDialogOpen = !state.joiningDate.isDialogOpen
                    )
                )
            }

            is StoreDetailsUiEvent.OnJoinDateChange -> {
                state = state.copy(
                    joiningDate = state.joiningDate.copy(
                        isDialogOpen = !state.joiningDate.isDialogOpen,
                        data = event.date,
                        isErr = false,
                        errText = UiText.DynamicString("")
                    ),
                    experience = DateUtils.calculateExperience(event.date)
                )
            }


            // qualification
            StoreDetailsUiEvent.OnQualificationDropDownClick -> {
                state = state.copy(
                    qualification = state.qualification.copy(
                        isDialogOpen = !state.qualification.isDialogOpen
                    )
                )
            }

            is StoreDetailsUiEvent.OnQualificationSelected -> {
                state = state.copy(
                    qualification = state.qualification.copy(
                        isDialogOpen = false,
                        isErr = false,
                        errText = UiText.DynamicString(""),
                        selected = state.qualification.all[event.index]
                    )
                )
            }

            is StoreDetailsUiEvent.PresentAddress -> {
                when (event) {
                    is StoreDetailsUiEvent.PresentAddress.HouseNumberChange -> {
                        state = state.copy(
                            presentAddress = state.presentAddress.copy(
                                houseNumber = state.presentAddress.houseNumber.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.PresentAddress.StreetChange -> {
                        state = state.copy(
                            presentAddress = state.presentAddress.copy(
                                street = state.presentAddress.street.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.PresentAddress.CityChange -> {
                        state = state.copy(
                            presentAddress = state.presentAddress.copy(
                                city = state.presentAddress.city.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.PresentAddress.ZipCodeChange -> {
                        state = state.copy(
                            presentAddress = state.presentAddress.copy(
                                zipCode = state.presentAddress.zipCode.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.PresentAddress.StateChange -> {
                        state = state.copy(
                            presentAddress = state.presentAddress.copy(
                                state = state.presentAddress.state.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.PresentAddress.CountryChange -> {
                        state = state.copy(
                            presentAddress = state.presentAddress.copy(
                                country = state.presentAddress.country.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }
                }
            }

            StoreDetailsUiEvent.OnSameAsPresentAddressClick -> {
                var isErr = false

                if (state.presentAddress.houseNumber.data.isEmpty()) {
                    state = state.copy(
                        presentAddress = state.presentAddress.copy(
                            houseNumber = state.presentAddress.houseNumber.copy(
                                isErr = true,
                                errText = UiText.StringResource(R.string.error_empty)
                            )
                        )
                    )

                    isErr = true
                }

                if (state.presentAddress.street.data.isEmpty()) {
                    state = state.copy(
                        presentAddress = state.presentAddress.copy(
                            street = state.presentAddress.street.copy(
                                isErr = true,
                                errText = UiText.StringResource(R.string.error_empty)
                            )
                        )
                    )

                    isErr = true
                }


                if (state.presentAddress.city.data.isEmpty()) {
                    state = state.copy(
                        presentAddress = state.presentAddress.copy(
                            city = state.presentAddress.city.copy(
                                isErr = true,
                                errText = UiText.StringResource(R.string.error_empty)
                            )
                        )
                    )

                    isErr = true
                }

                if (state.presentAddress.zipCode.data.isEmpty()) {
                    state = state.copy(
                        presentAddress = state.presentAddress.copy(
                            zipCode = state.presentAddress.zipCode.copy(
                                isErr = true,
                                errText = UiText.StringResource(R.string.error_empty)
                            )
                        )
                    )

                    isErr = true
                }

                if (state.presentAddress.state.data.isEmpty()) {
                    state = state.copy(
                        presentAddress = state.presentAddress.copy(
                            state = state.presentAddress.state.copy(
                                isErr = true,
                                errText = UiText.StringResource(R.string.error_empty)
                            )
                        )
                    )

                    isErr = true
                }


                if (state.presentAddress.country.data.isEmpty()) {
                    state = state.copy(
                        presentAddress = state.presentAddress.copy(
                            country = state.presentAddress.country.copy(
                                isErr = true,
                                errText = UiText.StringResource(R.string.error_empty)
                            )
                        )
                    )

                    isErr = true
                }


                if (isErr) {
                    viewModelScope.launch(Dispatchers.IO) {
                        _uiEvent.send(StoreDetailsUiAction.ShowToast(UiText.StringResource(R.string.error_field_are_empty_or_invalid)))
                    }

                    return
                }

                state = state.copy(
                    homeAddress = state.presentAddress
                )
            }

            is StoreDetailsUiEvent.HomeAddress -> {
                when (event) {
                    is StoreDetailsUiEvent.HomeAddress.HouseNumberChange -> {
                        state = state.copy(
                            homeAddress = state.homeAddress.copy(
                                houseNumber = state.homeAddress.houseNumber.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.HomeAddress.StreetChange -> {
                        state = state.copy(
                            homeAddress = state.homeAddress.copy(
                                street = state.homeAddress.street.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.HomeAddress.CityChange -> {
                        state = state.copy(
                            homeAddress = state.homeAddress.copy(
                                city = state.homeAddress.city.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.HomeAddress.ZipCodeChange -> {
                        state = state.copy(
                            homeAddress = state.homeAddress.copy(
                                zipCode = state.homeAddress.zipCode.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.HomeAddress.StateChange -> {
                        state = state.copy(
                            homeAddress = state.homeAddress.copy(
                                state = state.homeAddress.state.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }

                    is StoreDetailsUiEvent.HomeAddress.CountryChange -> {
                        state = state.copy(
                            homeAddress = state.homeAddress.copy(
                                country = state.homeAddress.country.copy(
                                    data = event.data,
                                    isErr = false,
                                    errText = UiText.DynamicString("")
                                )
                            )
                        )
                    }
                }
            }

            StoreDetailsUiEvent.OnContinueClick -> {
                if (state.isMakingApiCall) return

                state = state.copy(
                    isMakingApiCall = true
                )

                if (isErr()) {
                    state = state.copy(
                        isMakingApiCall = false
                    )

                    return
                }

                viewModelScope.launch(Dispatchers.IO) {
                    val response = client.authPost<StoreDetailsReq, SetDetailsRes>(
                        route = EndPoints.SetDetails.route,
                        body = state.toStoreDetailsReq(),
                        gson = gson
                    )

                    when (response) {
                        is Result.Error -> {
                            when (response.error) {
                                DataError.Network.NO_INTERNET -> {
                                    _uiEvent.send(
                                        StoreDetailsUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_internet)
                                        )
                                    )
                                }

                                else -> {
                                    _uiEvent.send(
                                        StoreDetailsUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_something_went_wrong)
                                        )
                                    )
                                }
                            }
                        }

                        is Result.Success -> {
                            when (response.data.status) {
                                TeacherDetailsSaveStatus.INVALID_REQ -> {
                                    _uiEvent.send(
                                        StoreDetailsUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_something_went_wrong)
                                        )
                                    )
                                }

                                TeacherDetailsSaveStatus.NOT_REGISTERED -> {
                                    _uiEvent.send(
                                        StoreDetailsUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_email_not_found)
                                        )
                                    )

                                    state = state.copy(
                                        email = state.email.copy(
                                            isErr = true,
                                            errText = UiText.StringResource(R.string.error_email_not_found)
                                        )
                                    )
                                }

                                else -> {
                                    val cookie =
                                        extractCookie(cookieManager)?.let { storeCookie(it, ds) }

                                    if (cookie == null) {
                                        _uiEvent.send(
                                            StoreDetailsUiAction.ShowToast(
                                                UiText.StringResource(R.string.error_something_went_wrong)
                                            )
                                        )
                                        state = state.copy(
                                            isMakingApiCall = false
                                        )

                                        return@launch
                                    }

                                    val localUser = LocalUser(
                                        name = state.userName.data.trim(),
                                        email = state.email.data.trim(),
                                        phone = state.phoneOne.data.trim(),
                                        department = state.department.selected.trim(),
                                        designation = state.designation.selected.trim(),
                                        isDepartmentInCharge = response.data.isDepartmentInCharge,
                                        userType = if (state.designation.selected.startsWith("S")) UserType.SACT
                                        else UserType.PERMANENT,
                                        sex = state.gender.selected.trim()
                                    )

                                    storeUser(ds, localUser)
                                    storeSignInState(Screens.Home, ds)

                                    _uiEvent.send(StoreDetailsUiAction.OnSuccess)
                                }
                            }
                        }
                    }

                    state = state.copy(
                        isMakingApiCall = false
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

        when (validator.isValidUserName(state.userName.data.trim())) {
            UserDataValidator.UserNameState.CAN_NOT_CONTAIN_UNDERSCORE -> {
                state = state.copy(
                    userName = state.userName.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_cant_contain_underscore)
                    )
                )

                isErr = true
            }

            UserDataValidator.UserNameState.CANT_BE_EMPTY -> {
                state = state.copy(
                    userName = state.userName.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )

                isErr = true
            }

            else -> Unit
        }

        if (!validator.isValidHrmsId(state.hrmsId.data.trim())) {
            state = state.copy(
                hrmsId = state.hrmsId.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.err_cant_contain_blank_sapce)
                )
            )

            isErr = true
        }

        if (!validator.isValidPhoneNumber(state.phoneOne.data.trim())) {
            state = state.copy(
                phoneOne = state.phoneOne.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_phone_number_not_valid)
                )
            )

            isErr = true
        }

        if (state.phoneTwo.data.isNotBlank()) {
            if (!validator.isValidPhoneNumber(state.phoneTwo.data.trim())) {
                state = state.copy(
                    phoneTwo = state.phoneTwo.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_phone_number_not_valid)
                    )
                )

                isErr = true
            }
        }

        if (state.bDay.data.isEmpty()) {
            state = state.copy(
                bDay = state.bDay.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (state.gender.selected.isEmpty()) {
            state = state.copy(
                gender = state.gender.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (state.designation.selected.isEmpty()) {
            state = state.copy(
                designation = state.designation.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (state.department.selected.isEmpty()) {
            state = state.copy(
                department = state.department.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (state.joiningDate.data.isEmpty()) {
            state = state.copy(
                joiningDate = state.joiningDate.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }

        if (state.qualification.selected.isEmpty()) {
            state = state.copy(
                qualification = state.qualification.copy(
                    isErr = true,
                    errText = UiText.StringResource(R.string.error_empty)
                )
            )

            isErr = true
        }


        // present address
        if (state.presentAddress.houseNumber.data.isEmpty()) {
            state = state.copy(
                presentAddress = state.presentAddress.copy(
                    houseNumber = state.presentAddress.houseNumber.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }

        if (state.presentAddress.street.data.isEmpty()) {
            state = state.copy(
                presentAddress = state.presentAddress.copy(
                    street = state.presentAddress.street.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }


        if (state.presentAddress.city.data.isEmpty()) {
            state = state.copy(
                presentAddress = state.presentAddress.copy(
                    city = state.presentAddress.city.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }

        if (state.presentAddress.zipCode.data.isEmpty()) {
            state = state.copy(
                presentAddress = state.presentAddress.copy(
                    zipCode = state.presentAddress.zipCode.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }

        if (state.presentAddress.state.data.isEmpty()) {
            state = state.copy(
                presentAddress = state.presentAddress.copy(
                    state = state.presentAddress.state.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }


        if (state.presentAddress.country.data.isEmpty()) {
            state = state.copy(
                presentAddress = state.presentAddress.copy(
                    country = state.presentAddress.country.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }


        // home address
        if (state.homeAddress.houseNumber.data.isEmpty()) {
            state = state.copy(
                homeAddress = state.homeAddress.copy(
                    houseNumber = state.homeAddress.houseNumber.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }

        if (state.homeAddress.street.data.isEmpty()) {
            state = state.copy(
                homeAddress = state.homeAddress.copy(
                    street = state.homeAddress.street.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }


        if (state.homeAddress.city.data.isEmpty()) {
            state = state.copy(
                homeAddress = state.homeAddress.copy(
                    city = state.homeAddress.city.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }

        if (state.homeAddress.zipCode.data.isEmpty()) {
            state = state.copy(
                homeAddress = state.homeAddress.copy(
                    zipCode = state.homeAddress.zipCode.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }

        if (state.homeAddress.state.data.isEmpty()) {
            state = state.copy(
                homeAddress = state.homeAddress.copy(
                    state = state.homeAddress.state.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }


        if (state.homeAddress.country.data.isEmpty()) {
            state = state.copy(
                homeAddress = state.homeAddress.copy(
                    country = state.homeAddress.country.copy(
                        isErr = true,
                        errText = UiText.StringResource(R.string.error_empty)
                    )
                )
            )

            isErr = true
        }

        if (isErr) {
            viewModelScope.launch {
                _uiEvent.send(
                    StoreDetailsUiAction.ShowToast(
                        UiText.StringResource(R.string.error_field_are_empty_or_invalid)
                    )
                )
            }
        }

        return isErr
    }
}
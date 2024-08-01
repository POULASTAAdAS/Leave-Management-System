package com.poulastaa.lms.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.BuildConfig
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.data.model.profile.ProfileRes
import com.poulastaa.lms.data.model.stoe_details.AddressType
import com.poulastaa.lms.data.remote.get
import com.poulastaa.lms.data.remote.uploadFile
import com.poulastaa.lms.data.repository.utils.NetworkConnectivityObserver
import com.poulastaa.lms.domain.repository.utils.ConnectivityObserver
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.presentation.utils.fileFromUri
import com.poulastaa.lms.ui.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val network: NetworkConnectivityObserver,
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient,
) : ViewModel() {
    var state by mutableStateOf(ProfileUiState())
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

        viewModelScope.launch {
            ds.readCookie().collectLatest {
                state = state.copy(
                    cookie = it
                )
            }
        }

        viewModelScope.launch {
            val user = ds.readUser().first()

            state = state.copy(
                userType = user.userType,
                name = user.name,
                personalDetails = state.personalDetails.copy(
                    email = user.email
                ),
                gender = user.sex
            )
        }
    }

    private val _uiEvent = Channel<ProfileUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var getDataJob: Job? = null

    fun startJob() {
        getDataJob?.cancel()
        getDataJob = getDataJob()
    }

    fun cancelJob() = getDataJob?.cancel()

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            ProfileUiEvent.DetailsEditClick -> {
                when (state.userType) {
                    UserType.PRINCIPLE,
                    UserType.HEAD_CLARK,
                    -> viewModelScope.launch {
                        viewModelScope.launch(Dispatchers.IO) {
                            _uiEvent.send(
                                ProfileUiAction.OnNavigate(
                                    screen = Screens.EditHeadDetails
                                )
                            )
                        }
                    }

                    else -> viewModelScope.launch(Dispatchers.IO) {
                        _uiEvent.send(
                            ProfileUiAction.OnNavigate(
                                screen = Screens.EditDetails,
                                args = mapOf(
                                    Screens.EditDetails.Args.NAME.title to state.name,
                                    Screens.EditDetails.Args.EMAIL.title to state.personalDetails.email,
                                    Screens.EditDetails.Args.PHONE_ONE.title to state.personalDetails.phoneOne,
                                    Screens.EditDetails.Args.PHONE_TWO.title to state.personalDetails.phoneTwo,
                                    Screens.EditDetails.Args.QUALIFICATION.title to state.personalDetails.qualification,
                                )
                            )
                        )
                    }
                }
            }

            ProfileUiEvent.OnHomeAddressEditClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiEvent.send(
                        ProfileUiAction.OnNavigate(
                            screen = Screens.EditAddress,
                            args = mapOf(
                                Screens.EditAddress.Args.TYPE.title to AddressType.HOME.name,
                                Screens.EditAddress.Args.HOUSE_NUM.title to state.homeAddress.houseNumber.replace(
                                    oldChar = '/',
                                    newChar = '_'
                                ),
                                Screens.EditAddress.Args.STREET.title to state.homeAddress.street,
                                Screens.EditAddress.Args.CITY.title to state.homeAddress.city,
                                Screens.EditAddress.Args.ZIP.title to state.homeAddress.zipcode,
                                Screens.EditAddress.Args.STATE.title to state.homeAddress.state
                            )
                        )
                    )
                }
            }

            ProfileUiEvent.OnPresentAddressEditClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiEvent.send(
                        ProfileUiAction.OnNavigate(
                            screen = Screens.EditAddress,
                            args = mapOf(
                                Screens.EditAddress.Args.TYPE.title to AddressType.PRESENT.name,
                                Screens.EditAddress.Args.HOUSE_NUM.title to state.presentAddress.houseNumber.replace(
                                    oldChar = '/',
                                    newChar = '_'
                                ),
                                Screens.EditAddress.Args.STREET.title to state.presentAddress.street,
                                Screens.EditAddress.Args.CITY.title to state.presentAddress.city,
                                Screens.EditAddress.Args.ZIP.title to state.presentAddress.zipcode,
                                Screens.EditAddress.Args.STATE.title to state.presentAddress.state

                            )
                        )
                    )
                }
            }

            is ProfileUiEvent.OnProfileEditClick -> {
                val file = event.uri?.let {
                    fileFromUri(
                        context = event.context,
                        uri = it
                    )
                } ?: return

                state = state.copy(
                    isProfilePicUpdating = true
                )

                viewModelScope.launch(Dispatchers.IO) {
                    val cookie = ds.readCookie().first()

                    val response = client.uploadFile<Boolean>(
                        route = EndPoints.UpdateProfilePic.route,
                        file = file,
                        gson = gson,
                        cookie = cookie
                    )

                    when (response) {
                        is Result.Error -> {
                            when (response.error) {
                                DataError.Network.NO_INTERNET -> {
                                    _uiEvent.send(
                                        ProfileUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_internet)
                                        )
                                    )
                                }

                                else -> {
                                    _uiEvent.send(
                                        ProfileUiAction.ShowToast(
                                            UiText.StringResource(R.string.error_something_went_wrong)
                                        )
                                    )
                                }
                            }
                        }

                        is Result.Success -> {
                            if (response.data) {
                                val user = ds.readUser().first()

                                ds.storeLocalUser(
                                    user = user.copy(
                                        profilePicUrl = BuildConfig.BASE_URL + EndPoints.GetProfilePic.route
                                    )
                                )

                                _uiEvent.send(
                                    ProfileUiAction.ShowToast(
                                        UiText.StringResource(R.string.profile_pic_updated)
                                    )
                                )
                            } else {
                                _uiEvent.send(
                                    ProfileUiAction.ShowToast(
                                        UiText.StringResource(R.string.error_something_went_wrong)
                                    )
                                )
                            }
                        }
                    }

                    state = state.copy(
                        isProfilePicUpdating = false
                    )
                }
            }

            ProfileUiEvent.LogOut -> {
                viewModelScope.launch(Dispatchers.IO) {
                    ds.clearAll()
                    _uiEvent.send(
                        ProfileUiAction.OnNavigate(
                            screen = Screens.Auth
                        )
                    )
                }
            }
        }
    }

    private fun getDataJob() = viewModelScope.launch(Dispatchers.IO) {
        val cookie = ds.readCookie().first()

        val user = ds.readUser().first()

        val res = client.get<ProfileRes>(
            route = EndPoints.GetDetails.route,
            params = listOf(
                "email" to user.email
            ),
            gson = gson,
            cookie = cookie,
            cookieManager = cookieManager,
            ds = ds
        )

        when (res) {
            is Result.Error -> {
                when (res.error) {
                    DataError.Network.NO_INTERNET -> {
                        _uiEvent.send(
                            ProfileUiAction.ShowToast(
                                UiText.StringResource(R.string.error_internet)
                            )
                        )
                    }

                    else -> {
                        _uiEvent.send(
                            ProfileUiAction.ShowToast(
                                UiText.StringResource(R.string.error_something_went_wrong)
                            )
                        )
                    }
                }
            }


            is Result.Success -> {
                val response = res.data


                val homeAddress = response.address.firstNotNullOf {
                    if (it.first == AddressType.HOME) it.second else null
                }.let {
                    ProfileUiAddress(
                        houseNumber = it.houseNumber,
                        street = it.street,
                        city = it.city,
                        zipcode = it.zipcode,
                        state = it.state,
                        country = it.country
                    )
                }

                val presentAddress = response.address.firstNotNullOf {
                    if (it.first == AddressType.PRESENT) it.second else null
                }.let {
                    ProfileUiAddress(
                        houseNumber = it.houseNumber,
                        street = it.street,
                        city = it.city,
                        zipcode = it.zipcode,
                        state = it.state,
                        country = it.country
                    )
                }

                state = state.copy(
                    name = response.name,
                    personalDetails = state.personalDetails.copy(
                        email = response.email,
                        phoneOne = response.phoneOne,
                        phoneTwo = response.phoneTwo,
                        qualification = response.qualification
                    ),
                    otherDetails = state.otherDetails.copy(
                        department = response.department,
                        exp = response.exp.replace("/", "-"),
                        joiningDate = response.joiningDate
                    ),
                    homeAddress = homeAddress,
                    presentAddress = presentAddress,

                    isMakingApiCall = false
                )
            }
        }
    }

    fun updateNameAndEmailIfChangedForHead() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = ds.readUser().first()

            state = state.copy(
                name = user.name,
                personalDetails = state.personalDetails.copy(
                    email = user.email
                )
            )
        }
    }
}
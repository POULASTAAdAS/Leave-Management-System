package com.poulastaa.lms.presentation.add_teacher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.AddTeacherReq
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.remote.post
import com.poulastaa.lms.domain.repository.auth.PatternValidator
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.ui.utils.UiText
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
class AddTeacherViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient,
    private val validator: PatternValidator,
) : ViewModel() {
    var state by mutableStateOf(AddTeacherUiState())
        private set


    private val _uiEvent = Channel<AddTeacherUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: AddTeacherUiEvent) {
        when (event) {
            is AddTeacherUiEvent.OnEmailChange -> {
                state = state.copy(
                    email = state.email.copy(
                        data = event.value,
                        isErr = false,
                        errText = UiText.DynamicString(""),
                    ),
                    isValidEmail = validator.matches(event.value)
                )
            }

            AddTeacherUiEvent.OnConformClick -> {
                if (!state.isValidEmail) {
                    state = state.copy(
                        email = state.email.copy(
                            isErr = true,
                            errText = UiText.StringResource(R.string.error_email_not_valid)
                        )
                    )

                    return
                }


                state = state.copy(
                    isMakingApiCall = true
                )

                viewModelScope.launch(Dispatchers.IO) {
                    val cookie = ds.readCookie().first()

                    val response = client.post<AddTeacherReq, Boolean>(
                        route = EndPoints.AddTeachers.route,
                        body = AddTeacherReq(
                            email = state.email.data
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
                                        AddTeacherUiAction.EmitToast(
                                            UiText.StringResource(R.string.error_internet)
                                        )
                                    )
                                }

                                else -> {
                                    _uiEvent.send(
                                        AddTeacherUiAction.EmitToast(
                                            UiText.StringResource(R.string.error_something_went_wrong)
                                        )
                                    )
                                }
                            }
                        }

                        is Result.Success -> {
                            when (response.data) {
                                true -> {
                                    _uiEvent.send(
                                        AddTeacherUiAction.EmitToast(
                                            UiText.StringResource(R.string.new_teacher_added)
                                        )
                                    )
                                }

                                false -> {
                                    _uiEvent.send(
                                        AddTeacherUiAction.EmitToast(
                                            UiText.StringResource(R.string.error_something_went_wrong)
                                        )
                                    )
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
}
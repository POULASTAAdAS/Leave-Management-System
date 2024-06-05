package com.poulastaa.lms.presentation.define_in_charge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.departmnet_in_charge.GetDepartmentInChargeRes
import com.poulastaa.lms.data.remote.get
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.ui.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

@HiltViewModel
class DefneInChargeViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient,
) : ViewModel() {
    var state by mutableStateOf(DefneInChargeUiState())

    private val _uiEvent = Channel<DefneInChargeUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var getDataJob: Job? = null

    fun onEvent(event: DefneInChargeUiEvent) {
        when (event) {
            DefneInChargeUiEvent.OnDepartmentToggle -> {
                state = state.copy(
                    departments = state.departments.copy(
                        isDialogOpen = state.departments.isDialogOpen.not()
                    )
                )
            }

            is DefneInChargeUiEvent.OnDepartmentSelect -> {
                state = state.copy(
                    isVisible = true,
                    isMakingApiCall = true,
                    departments = state.departments.copy(
                        isDialogOpen = state.departments.isDialogOpen.not(),
                        selected = state.departments.all[event.index]
                    )
                )

                viewModelScope.launch(Dispatchers.IO) {
                    getDataJob?.cancel()
                    getDataJob = getDataJob(department = state.departments.selected)
                }
            }

            DefneInChargeUiEvent.OnTeacherToggle -> {
                state = state.copy(
                    others = state.others.copy(
                        isDialogOpen = state.others.isDialogOpen.not()
                    )
                )
            }

            is DefneInChargeUiEvent.OnTeacherSelect -> {
                state = state.copy(
                    others = state.others.copy(
                        isDialogOpen = state.others.isDialogOpen.not(),
                        selected = state.others.teachers.map {
                            it.name
                        }[event.index]
                    )
                )
            }

            DefneInChargeUiEvent.OnConformClick -> {
                state = state.copy(
                    isDefiningHead = true
                )
            }
        }
    }

    private suspend fun getDataJob(department: String) = viewModelScope.launch(Dispatchers.IO) {
        val cookie = ds.readCookie().first()

        val response = client.get<GetDepartmentInChargeRes>(
            route = EndPoints.GetDepartmentInCharge.route,
            params = listOf(
                "department" to department
            ),
            gson = gson,
            cookieManager = cookieManager,
            ds = ds,
            cookie = cookie
        )

        when (response) {
            is Result.Error -> {
                when (response.error) {
                    DataError.Network.NO_INTERNET -> {
                        _uiEvent.send(
                            DefneInChargeUiAction.EmitToast(
                                UiText.StringResource(R.string.error_internet)
                            )
                        )
                    }

                    else -> {
                        _uiEvent.send(
                            DefneInChargeUiAction.EmitToast(
                                UiText.StringResource(R.string.error_something_went_wrong)
                            )
                        )
                    }
                }

                state = state.copy(
                    canReq = false,
                    isVisible = false,
                    isMakingApiCall = false,
                    isDefiningHead = false
                )
            }

            is Result.Success -> {
                state = state.copy(
                    current = response.data.current,
                    others = state.others.copy(
                        teachers = response.data.others.map {
                            DepartmentTeacher(
                                id = it.id,
                                name = it.name
                            )
                        }
                    )
                )
            }
        }

        state = state.copy(
            isMakingApiCall = false
        )
    }
}
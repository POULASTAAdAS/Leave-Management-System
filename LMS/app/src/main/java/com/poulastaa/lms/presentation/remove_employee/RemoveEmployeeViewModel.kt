package com.poulastaa.lms.presentation.remove_employee

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.remove_employee.ResponseTeacher
import com.poulastaa.lms.data.remote.get
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.Result
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
class RemoveEmployeeViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val client: OkHttpClient,
    private val cookieManager: CookieManager,
    private val gson: Gson,
) : ViewModel() {
    var state by mutableStateOf(RemoveEmployeeUiState())
        private set

    private var getTeacherJob: Job? = null


    init {
        viewModelScope.launch {
            val cookie = ds.readCookie().first()

            state = state.copy(
                cookie = cookie
            )
        }
    }

    private val _uiEvent = Channel<RemoveEmployeeUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: RemoveEmployeeUiEvent) {
        when (event) {
            RemoveEmployeeUiEvent.OnDepartmentToggle -> {
                state = state.copy(
                    department = state.department.copy(
                        isDialogOpen = !state.department.isDialogOpen
                    )
                )
            }

            is RemoveEmployeeUiEvent.OnDepartmentChange -> {
                state = state.copy(
                    department = state.department.copy(
                        isDialogOpen = false,
                        selected = state.department.all[event.index]
                    )
                )

                getTeacherJob?.cancel()
                getTeacherJob = getTeachers()
            }

            is RemoveEmployeeUiEvent.OnTeacherSelected -> {

            }

            is RemoveEmployeeUiEvent.OnConformClick -> {

            }
        }
    }

    private fun getTeachers() = viewModelScope.launch(Dispatchers.IO) {
        val result = client.get<List<ResponseTeacher>>(
            route = EndPoints.GetTeacherToDelete.route,
            params = listOf(
                "department" to state.department.selected,
            ),
            cookieManager = cookieManager,
            gson = gson,
            ds = ds,
            cookie = state.cookie
        )

        if (result is Result.Success) {
            if (result.data.isNotEmpty()) state = state.copy(
                teacher = result.data.map {
                    UiTeacher(
                        id = it.id,
                        name = it.name,
                        designation = it.designation,
                        profile = it.profile
                    )
                }
            )
        }
    }
}
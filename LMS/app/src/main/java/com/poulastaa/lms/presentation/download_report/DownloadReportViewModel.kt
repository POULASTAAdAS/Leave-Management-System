package com.poulastaa.lms.presentation.download_report

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.poulastaa.lms.BuildConfig
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.data.model.leave.GetDepartmentTeacher
import com.poulastaa.lms.data.model.report.ReportDataResponse
import com.poulastaa.lms.data.remote.get
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

@HiltViewModel
class DownloadReportViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val gson: Gson,
    private val cookieManager: CookieManager,
    private val client: OkHttpClient,
) : ViewModel() {
    var state by mutableStateOf(DownloadReportUiState())
        private set

    private var getTeacherJob: Job? = null

    init {
        viewModelScope.launch {
            val user = ds.readUser().first()

            state = when (user.userType) {
                UserType.PERMANENT -> {
                    getTeacherJob?.cancel()
                    getTeacherJob = getTeacher(user.department)

                    state.copy(
                        showDepartmentDropDown = user.isDepartmentInCharge,
                        department = state.department.copy(
                            selected = user.department
                        )
                    )
                }

                UserType.HEAD_CLARK -> {
                    getTeacherJob?.cancel()
                    getTeacherJob = getTeacher("NTS")

                    state.copy(
                        showDepartmentDropDown = true,
                        department = state.department.copy(
                            selected = "NTS"
                        )
                    )
                }

                else -> state.copy(
                    userTpe = state.userTpe
                )
            }
        }
    }

    fun onEvent(event: DownloadReportUiEvent) {
        when (event) {
            DownloadReportUiEvent.OnDepartmentToggle -> {
                state = state.copy(
                    department = state.department.copy(
                        isDialogOpen = !state.department.isDialogOpen
                    )
                )
            }

            is DownloadReportUiEvent.OnDepartmentChange -> {
                state = state.copy(
                    department = state.department.copy(
                        selected = state.department.all[event.index],
                        isDialogOpen = false
                    )
                )

                getTeacherJob?.cancel()
                getTeacherJob = getTeacher(state.department.selected)
            }

            DownloadReportUiEvent.OnTeacherToggle -> {
                state = state.copy(
                    teacher = state.teacher.copy(
                        isDialogOpen = !state.teacher.isDialogOpen
                    )
                )
            }

            is DownloadReportUiEvent.OnTeacherChange -> {
                state = state.copy(
                    teacher = state.teacher.copy(
                        selected = state.teacher.all[event.index],
                        isDialogOpen = false
                    )
                )
            }

            DownloadReportUiEvent.OnLeaveTypeToggle -> {
                state = state.copy(
                    leaveType = state.leaveType.copy(
                        isDialogOpen = !state.leaveType.isDialogOpen
                    )
                )
            }

            is DownloadReportUiEvent.OnLeaveTypeChange -> {
                state = state.copy(
                    leaveType = state.leaveType.copy(
                        selected = state.leaveType.all[event.index],
                        isDialogOpen = false
                    )
                )
            }

            DownloadReportUiEvent.OnViewReportClick -> {
                if (state.leaveType.selected.isBlank() || state.department.selected.isBlank()) return

                viewModelScope.launch(Dispatchers.IO) {
                    val response = client.get<List<ReportDataResponse>>(
                        route = EndPoints.GetReport.route,
                        params = listOf(
                            "leaveType" to state.leaveType.selected,
                            "department" to state.department.selected,
                            "teacher" to state.teacher.selected,
                        ),
                        cookieManager = cookieManager,
                        gson = gson,
                        cookie = ds.readCookie().first(),
                        ds = ds
                    )


                    if (response is Result.Success) {
                        state = state.copy(
                            prevResponse = emptyList()
                        )

                        state = state.copy(
                            prevResponse = response.data.map {
                                ReportUiState(
                                    department = it.department,
                                    name = it.name,
                                    listOfLeave = it.listOfLeave.map { leave ->
                                        LeaveData(
                                            applicationDate = "Application Date" to leave.applicationDate,
                                            reqType = "Leave Type" to leave.reqType,
                                            fromDate = "From Date" to leave.fromDate,
                                            toDate = "To Date" to leave.toDate,
                                            totalDays = "Total Days" to leave.totalDays
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
            }

            is DownloadReportUiEvent.OnDownloadClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (state.isMakingApiCall) return@launch

                    state = state.copy(
                        isMakingApiCall = true
                    )

                    val downloadManager =
                        event.context.getSystemService(DownloadManager::class.java)

                    val baseUrl = BuildConfig.BASE_URL + EndPoints.DownloadReport.route
                    val leaveType = state.leaveType.selected
                    val department = state.department.selected
                    val teacher = state.teacher.selected

                    val urlString =
                        "$baseUrl?leaveType=$leaveType&department=$department&teacher=$teacher"

                    val url = Uri.parse(urlString)

                    val req = DownloadManager.Request(url)
                        .addRequestHeader("Cookie", ds.readCookie().first())
                        .setMimeType("application/pdf")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setTitle("Report.pdf")
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            "Report.pdf"
                        )

                    downloadManager.enqueue(req)

                    state = state.copy(
                        isMakingApiCall = false
                    )
                }
            }
        }
    }

    private fun getTeacher(department: String) = viewModelScope.launch {
        val result = client.get<GetDepartmentTeacher>(
            route = EndPoints.GetDepartmentTeachers.route,
            gson = gson,
            cookie = ds.readCookie().first(),
            cookieManager = cookieManager,
            ds = ds,
            params = listOf(
                "department" to department
            )
        )

        if (result is Result.Success) {
            if (result.data.teacherName.isNotEmpty()) state = state.copy(
                teacher = state.teacher.copy(
                    all = result.data.teacherName.map { it.name }.plus("All")
                )
            )
        }
    }
}
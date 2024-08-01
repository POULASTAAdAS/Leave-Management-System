package com.poulastaa.lms.presentation.leave_approval

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.google.gson.Gson
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.data.model.leave.HandleLeaveReq
import com.poulastaa.lms.data.remote.LeaveApprovePagingSource
import com.poulastaa.lms.data.remote.post
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.presentation.store_details.ListHolder
import com.poulastaa.lms.ui.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

@HiltViewModel
class ApproveLeaveViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val client: OkHttpClient,
    private val pagingSource: LeaveApprovePagingSource,
) : ViewModel() {
    var state by mutableStateOf(ApproveLeaveUiState())
        private set

    private val _uiEvent = Channel<ApproveLeaveUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _leave: MutableStateFlow<PagingData<LeaveApproveCardInfo>> =
        MutableStateFlow(PagingData.empty())
    var leave = _leave.asStateFlow()
        private set

    fun loadData() {
        pagingSource.init(
            client = client,
            cookieManager = cookieManager,
            gson = gson,
            ds = ds
        )

        viewModelScope.launch {
            val user = ds.readUser().first()

            state = state.copy(
                userType = user.userType
            )

            val actionList =
                if (user.userType == UserType.PERMANENT ||
                    user.userType == UserType.HEAD_CLARK
                ) listOf(
                    "Accept And Forward",
                    "Reject"
                ) else listOf(
                    "Approve",
                    "Reject"
                )

            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false
                ),
                initialKey = 1
            ) {
                pagingSource
            }.flow.cachedIn(viewModelScope).collectLatest {
                _leave.value = it.map { res ->
                    LeaveApproveCardInfo(
                        id = res.leaveId,
                        reqDate = res.reqData,
                        name = res.name,
                        fromDate = res.fromDate,
                        toDate = res.toDate,
                        leaveType = res.leaveType,
                        totalDays = res.totalDays,
                        actions = ListHolder(
                            all = actionList
                        )
                    )
                }
            }
        }
    }

    fun onEvent(event: ApproveLeaveUiEvent) {
        when (event) {
            is ApproveLeaveUiEvent.OnItemToggle -> {
                _leave.value = _leave.value.map { item ->
                    if (item.id == event.id) item.copy(
                        isExpanded = !item.isExpanded
                    ) else item
                }
            }

            is ApproveLeaveUiEvent.OnActionToggle -> {
                _leave.value = _leave.value.map { item ->
                    if (item.id == event.id) item.copy(
                        actions = item.actions.copy(
                            isDialogOpen = item.actions.isDialogOpen.not()
                        )
                    ) else item
                }
            }

            is ApproveLeaveUiEvent.OnActionSelect -> {
                _leave.value = _leave.value.map { item ->
                    if (item.id == event.id) {
                        val action = item.actions.all[event.index]

                        item.copy(
                            actions = item.actions.copy(
                                isDialogOpen = item.actions.isDialogOpen.not(),
                                selected = action
                            ),
                            isRejected = action == "Reject"
                        )
                    } else item
                }
            }

            is ApproveLeaveUiEvent.OnCauseChange -> {
                _leave.value = _leave.value.map { item ->
                    if (item.id == event.id) item.copy(
                        cause = item.cause.copy(
                            data = event.value,
                            isErr = false,
                            errText = UiText.DynamicString("")
                        )
                    ) else item
                }
            }

            is ApproveLeaveUiEvent.OnConformClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (isErr(event.item)) return@launch

                    _leave.value = _leave.value.map { single ->
                        if (single.id == event.item.id) single.copy(
                            isSendingDataToServer = true
                        )
                        else single
                    }

                    val item = event.item

                    val cookie = ds.readCookie().first()
                    val response = client.post<HandleLeaveReq, Boolean>(
                        route = EndPoints.HandleLeave.route,
                        body = HandleLeaveReq(
                            leaveId = item.id,
                            action = item.actions.selected.trim(),
                            cause = item.cause.data.trim()
                        ),
                        cookie = cookie,
                        gson = gson,
                        cookieManager = cookieManager,
                        ds = ds
                    )


                    when (response) {
                        is Result.Error -> {
                            when (response.error) {
                                DataError.Network.NO_INTERNET -> {
                                    ApproveLeaveUiAction.EmitToast(
                                        UiText.StringResource(R.string.error_internet)
                                    )
                                }

                                else -> {
                                    _uiEvent.send(
                                        ApproveLeaveUiAction.EmitToast(
                                            UiText.StringResource(R.string.error_something_went_wrong)
                                        )
                                    )
                                }
                            }
                        }

                        is Result.Success -> {
                            if (response.data) {
                                _leave.value = _leave.value.filter {
                                    it.id != item.id
                                }
                            } else {
                                _leave.value = _leave.value.map { single ->
                                    if (single.id == item.id) single.copy(
                                        isSendingDataToServer = false
                                    )
                                    else single
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun isErr(item: LeaveApproveCardInfo): Boolean {
        var err = false

        if (item.isRejected && item.cause.data.trim().isEmpty()) err = true
        if (item.actions.selected.isEmpty()) err = true

        if (err) _uiEvent.send(
            ApproveLeaveUiAction.EmitToast(
                UiText.StringResource(R.string.err_cant_contain_blank_sapce)
            )
        )

        return err
    }
}
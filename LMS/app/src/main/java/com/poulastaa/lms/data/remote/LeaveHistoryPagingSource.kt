package com.poulastaa.lms.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.leave.LeaveHistoryInfoRes
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.presentation.leave_history.LeaveHistoryInfo
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

class LeaveHistoryPagingSource @Inject constructor(
    private val client: OkHttpClient,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val ds: DataStoreRepository
) : PagingSource<Int, LeaveHistoryInfo>() {
    override fun getRefreshKey(state: PagingState<Int, LeaveHistoryInfo>): Int? =
        state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LeaveHistoryInfo> {
        val cookie = ds.readCookie().first()
        val page = params.key ?: 1
        val pageSize = params.loadSize

        val response = client.get<List<LeaveHistoryInfoRes>>(
            route = EndPoints.GetHistoryLeaves.route,
            params = listOf(
                "page" to page.toString(),
                "pageSize" to pageSize.toString()
            ),
            gson = gson,
            cookie = cookie,
            cookieManager = cookieManager,
            ds = ds
        )

        return when (response) {
            is Result.Error -> {
                when (response.error) {
                    DataError.Network.NO_INTERNET -> {
                        LoadResult.Error(Exception("No Internet"))
                    }

                    else -> {
                        LoadResult.Error(Exception("Unknown Error"))
                    }
                }
            }

            is Result.Success -> {
                LoadResult.Page(
                    data = response.data.map {
                        LeaveHistoryInfo(
                            reqDate = it.reqDate,
                            leaveType = it.leaveType,
                            approveDate = it.approveDate,
                            status = it.status,
                            fromDate = it.fromDate,
                            toDate = it.toDate,
                            pendingEnd = it.pendingEnd,
                            totalDays = it.totalDays
                        )
                    },
                    prevKey = if (page == 1) null else page.minus(1),
                    nextKey = if (response.data.isEmpty()) null else page.plus(1)
                )
            }
        }
    }
}
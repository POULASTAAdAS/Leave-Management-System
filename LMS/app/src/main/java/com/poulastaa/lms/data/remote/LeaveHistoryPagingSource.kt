package com.poulastaa.lms.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.leave.LeaveInfoRes
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import com.poulastaa.lms.presentation.leave_history.LeaveInfo
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

class LeaveHistoryPagingSource @Inject constructor(
    private val client: OkHttpClient,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val ds: DataStoreRepository
) : PagingSource<Int, LeaveInfo>() {
    override fun getRefreshKey(state: PagingState<Int, LeaveInfo>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LeaveInfo> {
        val cookie = ds.readCookie().first()
        val page = params.key ?: 1

        val response = client.get<List<LeaveInfoRes>>(
            route = EndPoints.GetLeaves.route,
            params = listOf(
                "page" to page.toString(),
                "pageSize" to "20"
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
                        LeaveInfo(
                            reqDate = it.reqDate,
                            leaveType = it.leaveType,
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
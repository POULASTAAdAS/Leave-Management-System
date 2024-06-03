package com.poulastaa.lms.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.leave.LeaveApproveInfoRes
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

class LeaveApprovePagingSource @Inject constructor() : PagingSource<Int, LeaveApproveInfoRes>() {
    private lateinit var client: OkHttpClient
    private lateinit var cookieManager: CookieManager
    private lateinit var gson: Gson
    private lateinit var ds: DataStoreRepository

    fun init(
        client: OkHttpClient,
        cookieManager: CookieManager,
        gson: Gson,
        ds: DataStoreRepository
    ) {
        this.client = client
        this.cookieManager = cookieManager
        this.gson = gson
        this.ds = ds
    }

    override fun getRefreshKey(state: PagingState<Int, LeaveApproveInfoRes>): Int? =
        state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LeaveApproveInfoRes> {
        val cookie = ds.readCookie().first()
        val page = params.key ?: 1
        val pageSize = params.loadSize

        val response = client.get<List<LeaveApproveInfoRes>>(
            route = EndPoints.GetApproveLeaves.route,
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
                    data = response.data,
                    prevKey = if (page == 1) null else page.minus(1),
                    nextKey = if (response.data.isEmpty()) null else page.plus(1)
                )
            }
        }
    }
}
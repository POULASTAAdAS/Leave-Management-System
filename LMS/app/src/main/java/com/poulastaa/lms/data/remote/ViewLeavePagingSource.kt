package com.poulastaa.lms.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.data.model.leave.ViewLeaveSingleRes
import com.poulastaa.lms.domain.repository.utils.DataStoreRepository
import com.poulastaa.lms.domain.utils.DataError
import com.poulastaa.lms.domain.utils.Result
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Inject

class ViewLeavePagingSource @Inject constructor(
    private val client: OkHttpClient,
    private val cookieManager: CookieManager,
    private val gson: Gson,
    private val ds: DataStoreRepository,
) : PagingSource<Int, ViewLeaveSingleRes>() {
    private var department: String = "All"

    fun setDepartment(department: String) {
        this.department = department
    }

    override fun getRefreshKey(state: PagingState<Int, ViewLeaveSingleRes>): Int? =
        state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ViewLeaveSingleRes> {
        val cookie = ds.readCookie().first()
        val page = params.key ?: 1
        val pageSize = params.loadSize

        val response = client.get<List<ViewLeaveSingleRes>>(
            route = EndPoints.GetViewLeaves.route,
            params = listOf(
                "department" to department,
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
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (response.data.isEmpty()) null else page + 1
                )
            }
        }
    }
}
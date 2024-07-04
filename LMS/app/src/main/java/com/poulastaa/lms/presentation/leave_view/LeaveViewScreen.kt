package com.poulastaa.lms.presentation.leave_view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.poulastaa.lms.R
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent
import kotlin.random.Random

val headingList = listOf(
    "Request Date",
    "Name",
    "From Date",
    "To Date",
    "Total Days",
    "Leave Type",
    "Status",
    "Cause"
)

@Composable
fun LeaveViewRootScreen(
    viewModel: LeaveViewViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is LeaveViewUiAction.EmitToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val leave = viewModel.leave.collectAsLazyPagingItems()

    LeaveViewScreen(
        leave = leave,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun LeaveViewScreen(
    leave: LazyPagingItems<ViewLeaveSingleData>,
    onEvent: (LeaveViewUiEvent) -> Unit,
    navigateBack: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onBackground
                        )
                    )
                )
                .padding(it)
                .padding(horizontal = MaterialTheme.dimens.medium1)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = navigateBack, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = ArrowBackIcon,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

                Text(
                    text = stringResource(id = R.string.leave_view_heading),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
            }

            Spacer(modifier = Modifier.heightIn(MaterialTheme.dimens.medium1))


        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        val list = (1..10).map {
            ViewLeaveSingleData(
                department = "Department $it",
                isExpanded = Random.nextBoolean().not(),
                listOfLeave = (1..10).map {
                    ViewLeaveInfo(
                        reqData = "2024-10-10",
                        name = "Poulastaa Das",
                        fromDate = "2024-10-10",
                        toDate = "2024-10-10",
                        totalDays = "10",
                        leaveType = "Casual Leave",
                        status = "Approved",
                        cause = "cause"
                    )
                }
            )
        }


        val pager = Pager(
            config = PagingConfig(pageSize = 10),
        ) {
            object : PagingSource<Int, ViewLeaveSingleData>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ViewLeaveSingleData> {
                    return LoadResult.Page(
                        data = list,
                        prevKey = null,
                        nextKey = null
                    )
                }

                override fun getRefreshKey(state: PagingState<Int, ViewLeaveSingleData>): Int? =
                    null
            }
        }
        val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

        LeaveViewScreen(
            leave = lazyPagingItems,
            onEvent = {},
            navigateBack = { }
        )
    }
}
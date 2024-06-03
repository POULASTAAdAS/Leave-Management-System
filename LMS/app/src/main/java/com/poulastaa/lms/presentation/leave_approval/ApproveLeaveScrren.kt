package com.poulastaa.lms.presentation.leave_approval

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.leave_approval.components.ApproveLeaveCard
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent
import kotlin.random.Random

@Composable
fun ApproveLeaveRootScreen(
    viewModel: ApproveLeaveViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.loadData()
    }

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is ApproveLeaveUiAction.EmitToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val leave = viewModel.leave.collectAsLazyPagingItems()

    ApproveLeaveScreen(
        onEvent = viewModel::onEvent,
        leave = leave,
        navigateBack = navigateBack
    )
}

@Composable
private fun ApproveLeaveScreen(
    leave: LazyPagingItems<LeaveApproveCardInfo>,
    onEvent: (ApproveLeaveUiEvent) -> Unit,
    navigateBack: () -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onBackground
                        )
                    )
                )
                .fillMaxSize()
                .padding(it)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = MaterialTheme.dimens.medium1),
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
                    text = stringResource(id = R.string.leave_history_heading),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
            }

            if (leave.itemCount > 0)
                LazyColumn(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
                    contentPadding = PaddingValues(MaterialTheme.dimens.medium1)
                ) {
                    items(leave.itemCount) { index ->
                        leave[index]?.let { item ->
                            ApproveLeaveCard(
                                modifier = Modifier.clickable {
                                    onEvent(ApproveLeaveUiEvent.OnItemToggle(item.id))
                                },
                                leaveApproveCardInfo = item,
                                onEvent = onEvent,
                            )
                        }
                    }
                }
            else {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(id = R.string.no_leave_to_approve),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    modifier = Modifier
                        .padding(MaterialTheme.dimens.medium1)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        val list = (1..10).map {
            LeaveApproveCardInfo(
                id = it.toLong(),
                reqDate = "2024-10-10",
                name = "Poulastaa Das",
                fromDate = "2024-10-10",
                toDate = "2024-10-10",
                totalDays = "10",
                isExpanded = Random.nextBoolean().or(false),
                leaveType = "Casual Leave"
            )
        }

        val pager = Pager(
            config = PagingConfig(pageSize = 10),
        ) {
            object : PagingSource<Int, LeaveApproveCardInfo>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LeaveApproveCardInfo> {
                    return LoadResult.Page(
                        data = list,
                        prevKey = null,
                        nextKey = null
                    )
                }

                override fun getRefreshKey(state: PagingState<Int, LeaveApproveCardInfo>): Int? =
                    null
            }
        }
        val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

        ApproveLeaveScreen(
            leave = lazyPagingItems,
            onEvent = {},
        ) {

        }
    }
}




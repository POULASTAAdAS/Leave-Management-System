package com.poulastaa.lms.presentation.leave_view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.leave_view.components.LeaveViewCard
import com.poulastaa.lms.presentation.store_details.ListHolder
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.ArrowDropDownIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent
import kotlin.random.Random


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
        state = viewModel.state,
        leave = leave,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun LeaveViewScreen(
    state: LeaveViewUiState,
    leave: LazyPagingItems<ViewLeaveSingleData>,
    onEvent: (LeaveViewUiEvent) -> Unit,
    navigateBack: () -> Unit,
) {
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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.select_department),
                    color = MaterialTheme.colorScheme.background,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        modifier = Modifier.clickable(
                            onClick = {
                                onEvent(LeaveViewUiEvent.OnDepartmentToggle)
                            },
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ),
                        value = state.department.selected,
                        onValueChange = {},
                        enabled = false,
                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = Color.Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.background,
                            disabledIndicatorColor = MaterialTheme.colorScheme.background,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.background
                        ),
                        shape = MaterialTheme.shapes.medium,
                        maxLines = 1,
                        trailingIcon = {
                            Icon(
                                imageVector = ArrowDropDownIcon,
                                contentDescription = null,
                                modifier = Modifier.rotate(
                                    if (state.department.isDialogOpen) 180f else 0f
                                )
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = state.department.isDialogOpen,
                        onDismissRequest = {
                            onEvent(LeaveViewUiEvent.OnDepartmentToggle)
                        },
                        modifier = Modifier
                            .fillMaxWidth(.55f)
                            .heightIn(
                                max = 300.dp
                            )
                            .background(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                    ) {
                        state.department.all.forEachIndexed { index, s ->
                            DropdownMenuItem(
                                modifier = Modifier.background(
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                text = {
                                    Text(
                                        text = s,
                                        color = MaterialTheme.colorScheme.background
                                    )
                                },
                                onClick = {
                                    onEvent(LeaveViewUiEvent.OnDepartmentChange(index))
                                }
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(id = R.string.select_teacher)}: ",
                    color = MaterialTheme.colorScheme.background,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        modifier = Modifier.clickable(
                            onClick = {
                                onEvent(LeaveViewUiEvent.OnTeacherToggle)
                            },
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ),
                        value = state.teacher.selected,
                        onValueChange = {},
                        enabled = false,
                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = Color.Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.background,
                            disabledIndicatorColor = MaterialTheme.colorScheme.background,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.background,
                            disabledLabelColor = MaterialTheme.colorScheme.background
                        ),
                        shape = MaterialTheme.shapes.medium,
                        maxLines = 1,
                        trailingIcon = {
                            Icon(
                                imageVector = ArrowDropDownIcon,
                                contentDescription = null,
                                modifier = Modifier.rotate(
                                    if (state.teacher.isDialogOpen) 180f else 0f
                                )
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = state.teacher.isDialogOpen,
                        onDismissRequest = {
                            onEvent(LeaveViewUiEvent.OnTeacherToggle)
                        },
                        modifier = Modifier
                            .fillMaxWidth(.55f)
                            .heightIn(
                                max = 300.dp
                            )
                            .background(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                    ) {
                        state.teacher.all.forEachIndexed { index, s ->
                            DropdownMenuItem(
                                modifier = Modifier.background(
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                text = {
                                    Text(
                                        text = s,
                                        color = MaterialTheme.colorScheme.background
                                    )
                                },
                                onClick = {
                                    onEvent(LeaveViewUiEvent.OnTeacherChange(index))
                                }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = MaterialTheme.dimens.medium1)
                    .border(
                        width = 1.3.dp,
                        color = MaterialTheme.colorScheme.background,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                LazyColumn(
                    modifier = Modifier,
                    contentPadding = PaddingValues(MaterialTheme.dimens.small3),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3)
                ) {
                    items(leave.itemCount) { index ->
                        leave[index]?.let { item ->
                            LeaveViewCard(
                                modifier = Modifier.clickable {
                                    onEvent(LeaveViewUiEvent.OnLeaveToggle(item.department))
                                },
                                leaveViewCard = item
                            )
                        }
                    }
                }
            }
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
            state = LeaveViewUiState(
                department = ListHolder(
                    isDialogOpen = false,
                    all = listOf(
                        "ASP(Advertisement and Sales Promotion)",
                        "Bengali",
                        "Botany",
                        "Chemistry",
                        "Commerce",
                        "Computer Science",
                        "Economics",
                        "Education",
                        "Electronic Science",
                        "English",
                        "Environmental Science",
                        "Food & Nutrition",
                        "Geography",
                        "Hindi",
                        "History",
                        "Journalism & Mass Com.",
                        "Mathematics",
                        "Philosophy",
                        "Physical Education",
                        "Physics",
                        "Physiology",
                        "Sanskrit",
                        "Sociology",
                        "Urdu",
                        "Zoology",
                        "Other"
                    )
                )
            ),
            leave = lazyPagingItems,
            onEvent = {},
            navigateBack = { }
        )
    }
}
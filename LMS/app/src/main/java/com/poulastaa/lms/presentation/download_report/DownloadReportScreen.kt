package com.poulastaa.lms.presentation.download_report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsListSelector
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun DownloadReportRootScreen(
    viewModel: DownloadReportViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    DownloadReportScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent
    ) {
        navigateBack()
    }
}


@Composable
private fun DownloadReportScreen(
    state: DownloadReportUiState,
    onEvent: (DownloadReportUiEvent) -> Unit,
    navigateBack: () -> Unit,
) {
    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedTextColor = MaterialTheme.colorScheme.background,
        unfocusedTextColor = MaterialTheme.colorScheme.background,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.background,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.background,
        disabledTrailingIconColor = MaterialTheme.colorScheme.background,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
        focusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
        focusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedLabelColor = MaterialTheme.colorScheme.background,
        cursorColor = MaterialTheme.colorScheme.background,
        errorTextColor = MaterialTheme.colorScheme.error,
    )

    ScreenWrapper(
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = navigateBack,
                colors = IconButtonDefaults.iconButtonColors(
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
                text = stringResource(id = R.string.download_report),
                color = MaterialTheme.colorScheme.primaryContainer,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (!state.isDepartmentHead) {
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

            StoreDetailsListSelector(
                modifier = Modifier.fillMaxWidth(.9f),
                label = stringResource(id = R.string.department),
                text = state.department.selected,
                isOpen = state.department.isDialogOpen,
                list = state.department.all,
                color = textFieldColors,
                onToggle = {
                    onEvent(DownloadReportUiEvent.OnDepartmentToggle)
                },
                onCancel = {
                    onEvent(DownloadReportUiEvent.OnDepartmentToggle)
                },
                onSelected = {
                    onEvent(DownloadReportUiEvent.OnDepartmentChange(it))
                }
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            StoreDetailsListSelector(
                textModifier = Modifier.fillMaxWidth(.6f),
                modifier = Modifier.fillMaxWidth(.6f),
                label = stringResource(id = R.string.leave_type),
                text = state.leaveType.selected,
                isOpen = state.leaveType.isDialogOpen,
                list = state.leaveType.all,
                color = textFieldColors,
                onToggle = {
                    onEvent(DownloadReportUiEvent.OnLeaveTypeToggle)
                },
                onCancel = {
                    onEvent(DownloadReportUiEvent.OnLeaveTypeToggle)
                },
                onSelected = {
                    onEvent(DownloadReportUiEvent.OnLeaveTypeChange(it))
                }
            )

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                ),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    onEvent(DownloadReportUiEvent.OnViewReportClick)
                }
            ) {
                Text(
                    text = "view\nReport",
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        Text(
            text = stringResource(id = R.string.preview),
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.onBackground.copy(.8f),
                    shape = MaterialTheme.shapes.small
                )
                .weight(1f)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.small
                )
                .horizontalScroll(rememberScrollState()),
            contentPadding = PaddingValues(MaterialTheme.dimens.medium1)
        ) {
            itemsIndexed(state.prevResponse) { index, data ->
                if (index == 0 && data.department != null) {
                    Text(
                        text = data.department,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = MaterialTheme.colorScheme.background
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
                }

                Text(
                    text = data.name,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

                data.listOfLeave.forEach {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1)
                    ) {
                        ColumnItem(pair = it.id)

                        ColumnItem(pair = it.applicationDate)

                        ColumnItem(pair = it.reqType)

                        ColumnItem(pair = it.fromDate)

                        ColumnItem(pair = it.toDate)

                        ColumnItem(pair = it.totalDays)
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        val context= LocalContext.current

        Button(
            modifier = Modifier.fillMaxWidth(.7f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp
            ),
            shape = MaterialTheme.shapes.small,
            onClick = {
                onEvent(DownloadReportUiEvent.OnDownloadClick(context))
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.download_report),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(if (state.isMakingApiCall) 0f else 1f)
                )

                CircularProgressIndicator(
                    modifier = Modifier.alpha(if (state.isMakingApiCall) 1f else 0f)
                )
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))
    }
}

@Composable
fun ColumnItem(
    pair: Pair<String, String>,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = pair.first,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            color = MaterialTheme.colorScheme.background,
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

        Text(
            text = pair.second,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        DownloadReportScreen(
            state = DownloadReportUiState(
                prevResponse = (1..3).map {
                    ReportUiState(
                        department = "Department",
                        name = "Name $it",
                        listOfLeave = (1..8).map {
                            LeaveData(
                                id = Pair("", it.toString()),
                                applicationDate = Pair(
                                    first = "Application Date",
                                    second = "2024-10-10"
                                ),
                                reqType = Pair(
                                    "Leave Type",
                                    "CL"
                                ),
                                fromDate = Pair(
                                    "From Date",
                                    "2024-10-05"
                                ),
                                toDate = Pair(
                                    "To Date",
                                    "2024-10-10"
                                ),
                                totalDays = Pair(
                                    "Total Days",
                                    "2"
                                )
                            )
                        }
                    )
                }
            ),
            onEvent = {}
        ) {

        }
    }
}

package com.poulastaa.lms.presentation.leave_approval.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.leave_approval.ApproveLeaveUiEvent
import com.poulastaa.lms.presentation.leave_approval.LeaveApproveCardInfo
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsListSelector
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsTextFiled
import com.poulastaa.lms.ui.theme.ArrowDropDownIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun ApproveLeaveCard(
    modifier: Modifier = Modifier,
    leaveApproveCardInfo: LeaveApproveCardInfo,
    onEvent: (ApproveLeaveUiEvent) -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.medium1)
        ) {
            Column(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1)
                ) {
                    SingleColumn(
                        headerOne = stringResource(id = R.string.leave_req_date),
                        headerTwo = stringResource(id = R.string.from_date),
                        nameOne = leaveApproveCardInfo.reqDate,
                        nameTwo = leaveApproveCardInfo.fromDate
                    )

                    SingleColumn(
                        headerOne = stringResource(id = R.string.username),
                        headerTwo = stringResource(id = R.string.to_date),
                        nameOne = leaveApproveCardInfo.name,
                        nameTwo = leaveApproveCardInfo.toDate
                    )

                    SingleColumn(
                        headerOne = stringResource(id = R.string.leave_type),
                        headerTwo = stringResource(id = R.string.total_days),
                        nameOne = leaveApproveCardInfo.leaveType,
                        nameTwo = leaveApproveCardInfo.totalDays
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    modifier = Modifier.rotate(if (leaveApproveCardInfo.isExpanded) 180f else 0f),
                    imageVector = ArrowDropDownIcon,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = leaveApproveCardInfo.isExpanded) {
                val focusManager = LocalFocusManager.current

                Column {
                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Spacer(modifier = Modifier.fillMaxWidth(.4f))

                        StoreDetailsListSelector(
                            modifier = Modifier
                                .fillMaxWidth(.5f),
                            label = stringResource(id = R.string.action),
                            text = leaveApproveCardInfo.actions.selected,
                            isOpen = leaveApproveCardInfo.actions.isDialogOpen,
                            list = leaveApproveCardInfo.actions.all,
                            color = TextFieldDefaults.colors(
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
                                errorTextColor = MaterialTheme.colorScheme.error
                            ),
                            onCancel = {
                                onEvent(ApproveLeaveUiEvent.OnActionToggle(leaveApproveCardInfo.id))
                            },
                            onToggle = {
                                onEvent(ApproveLeaveUiEvent.OnActionToggle(leaveApproveCardInfo.id))
                            },
                            onSelected = { index ->
                                if (!leaveApproveCardInfo.isSendingDataToServer) onEvent(
                                    ApproveLeaveUiEvent.OnActionSelect(
                                        index = index,
                                        id = leaveApproveCardInfo.id
                                    )
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3 + MaterialTheme.dimens.small1))


                    AnimatedVisibility(visible = leaveApproveCardInfo.isRejected) {
                        Column {
                            StoreDetailsTextFiled(
                                modifier = Modifier.fillMaxWidth(),
                                text = leaveApproveCardInfo.cause.data,
                                label = stringResource(id = R.string.cause),
                                onValueChange = {
                                    if (!leaveApproveCardInfo.isSendingDataToServer) onEvent(
                                        ApproveLeaveUiEvent.OnCauseChange(
                                            value = it,
                                            id = leaveApproveCardInfo.id
                                        )
                                    )
                                },
                                keyboardType = KeyboardType.Text,
                                isErr = leaveApproveCardInfo.cause.isErr,
                                singleLine = false,
                                errText = leaveApproveCardInfo.cause.errText.asString(),
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            )

                            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(.6f),
                            onClick = {
                                focusManager.clearFocus()
                                onEvent(ApproveLeaveUiEvent.OnConformClick(leaveApproveCardInfo))
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.background,
                                disabledContainerColor = Color.Transparent
                            ),
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ),
                            enabled = !leaveApproveCardInfo.isSendingDataToServer
                        ) {
                            Box(
                                modifier = Modifier.padding(MaterialTheme.dimens.small2),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .alpha(if (leaveApproveCardInfo.isSendingDataToServer) 1f else 0f),
                                    strokeWidth = 2.dp,
                                    strokeCap = StrokeCap.Round,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                                Text(
                                    modifier = Modifier
                                        .alpha(if (leaveApproveCardInfo.isSendingDataToServer) 0f else 1f),
                                    text = stringResource(id = R.string.submit).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 4.sp,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleColumn(
    modifier: Modifier = Modifier,
    headerOne: String,
    headerTwo: String,
    nameOne: String,
    nameTwo: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1)
    ) {
        TextCard(
            header = headerOne,
            name = nameOne
        )

        TextCard(
            header = headerTwo,
            name = nameTwo
        )
    }
}

@Composable
private fun TextCard(
    modifier: Modifier = Modifier,
    header: String,
    name: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = header,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

        Text(
            text = name,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TestThem {
        Column(
            modifier = Modifier.padding(MaterialTheme.dimens.medium1)
        ) {
            ApproveLeaveCard(
                leaveApproveCardInfo = LeaveApproveCardInfo(
                    id = 1,
                    reqDate = "2024-10-10",
                    name = "Poulastaa Das",
                    fromDate = "2024-10-10",
                    toDate = "2024-10-10",
                    totalDays = "10",
                    isExpanded = true,
                    isSendingDataToServer = false,
                    leaveType = "Casual Leave"
                ),
                onEvent = {}
            )
        }
    }
}
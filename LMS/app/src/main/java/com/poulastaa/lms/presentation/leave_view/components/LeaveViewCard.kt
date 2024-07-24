package com.poulastaa.lms.presentation.leave_view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.leave_view.ViewLeaveInfo
import com.poulastaa.lms.presentation.leave_view.ViewLeaveSingleData
import com.poulastaa.lms.ui.theme.ArrowDropDownIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens


val headingList = listOf(
    "Application Date",
    "Name",
    "Leave Type",
    "From Date",
    "To Date",
    "Total Days",
    "Leave Type",
    "Status",
    "Cause"
)


@Composable
fun LeaveViewCard(
    modifier: Modifier = Modifier,
    leaveViewCard: ViewLeaveSingleData,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(MaterialTheme.dimens.medium1),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = leaveViewCard.department,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        modifier = Modifier.rotate(
                            if (leaveViewCard.isExpanded) 180f else 0f
                        ),
                        imageVector = ArrowDropDownIcon,
                        contentDescription = null
                    )
                }
            }
        }

        AnimatedVisibility(visible = leaveViewCard.isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.dimens.medium1)
            ) {
                repeat(leaveViewCard.listOfLeave.size) {
                    OneItem(
                        viewLeaveInfo = leaveViewCard.listOfLeave[it],
                        isLast = it == leaveViewCard.listOfLeave.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
fun OneItem(
    modifier: Modifier = Modifier,
    viewLeaveInfo: ViewLeaveInfo,
    isLast: Boolean,
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Heading(
            text = stringResource(id = R.string.application_date),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

        ItemText(
            text = viewLeaveInfo.reqData,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Heading(
            text = stringResource(id = R.string.username),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

        ItemText(
            text = viewLeaveInfo.name,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Heading(
            text = stringResource(id = R.string.leave_type),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

        ItemText(
            text = viewLeaveInfo.leaveType,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Heading(
            text = stringResource(id = R.string.from_date),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

        ItemText(
            text = viewLeaveInfo.fromDate,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Heading(
            text = stringResource(id = R.string.to_date),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

        ItemText(
            text = viewLeaveInfo.toDate,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Heading(
            text = stringResource(id = R.string.total_days),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

        ItemText(
            text = viewLeaveInfo.totalDays,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Heading(
            text = stringResource(id = R.string.state),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

        ItemText(
            text = viewLeaveInfo.status,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Heading(
            text = stringResource(id = R.string.cause),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

        ItemText(
            text = viewLeaveInfo.cause,
            modifier = Modifier.weight(1f)
        )
    }

    if (!isLast) {
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))
    }
}

@Composable
private fun Heading(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}

@Composable
private fun ItemText(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = TextAlign.Start,
        maxLines = 2
    )
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    TestThem {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primary.copy(.5f))
                .padding(MaterialTheme.dimens.medium1),
        ) {
            LeaveViewCard(
                leaveViewCard = ViewLeaveSingleData(
                    department = "Computer Science",
                    isExpanded = true,
                    listOfLeave = listOf(
                        ViewLeaveInfo(
                            reqData = "2024-10-10",
                            name = "Poulastaa Das",
                            leaveType = "leave type",
                            fromDate = "2024-10-10",
                            toDate = "2024-10-10",
                            totalDays = "10",
                            status = "Approved",
                            cause = "cause"
                        )
                    )
                )
            )
        }
    }
}
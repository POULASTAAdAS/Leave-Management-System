package com.poulastaa.lms.presentation.leave_view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.presentation.leave_view.ViewLeaveSingleData
import com.poulastaa.lms.ui.theme.ArrowDropDownIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun LeaveViewCard(
    modifier: Modifier = Modifier,
    headingList: List<String>,
    leaveViewCard: ViewLeaveSingleData
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
                    .padding(
                        start = MaterialTheme.dimens.medium1,
                        end = MaterialTheme.dimens.small1
                    )
                    .padding(vertical = MaterialTheme.dimens.medium1)
                    .horizontalScroll(rememberScrollState()),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(headingList.size) {
                        Text(
                            modifier = Modifier
                                .widthIn(min = 90.dp)
                                .align(Alignment.CenterVertically),
                            text = headingList[it],
                            fontWeight = FontWeight.SemiBold,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))


                repeat(leaveViewCard.listOfLeave.size) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.dimens.small3),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ItemText(text = leaveViewCard.listOfLeave[it].reqData)

                        ItemText(text = leaveViewCard.listOfLeave[it].name)

                        ItemText(text = leaveViewCard.listOfLeave[it].fromDate)

                        ItemText(text = leaveViewCard.listOfLeave[it].toDate)

                        ItemText(text = leaveViewCard.listOfLeave[it].totalDays)

                        ItemText(text = leaveViewCard.listOfLeave[it].leaveType)

                        ItemText(text = leaveViewCard.listOfLeave[it].status)

                        ItemText(text = leaveViewCard.listOfLeave[it].cause)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ItemText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier
            .widthIn(min = 90.dp)
            .align(Alignment.CenterVertically),
        text = text,
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        textAlign = TextAlign.Center
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
                headingList = listOf(
                    "Request Date",
                    "Name",
                    "From Date",
                    "To Date",
                    "Total Days",
                    "Leave Type",
                    "Status",
                    "Cause"
                ),
                leaveViewCard = ViewLeaveSingleData(
                    department = "Computer Science",
                    isExpanded = true,
                    listOfLeave = listOf()
                )
            )
        }
    }
}
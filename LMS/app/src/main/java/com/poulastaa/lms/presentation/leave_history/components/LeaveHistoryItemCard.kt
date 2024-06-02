package com.poulastaa.lms.presentation.leave_history.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.presentation.leave_history.LeaveInfo
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import java.time.LocalDate

@Composable
fun LeaveHistoryItemCard(
    modifier: Modifier = Modifier,
    leaveInfo: LeaveInfo
) {
    Card(
        modifier = Modifier
            .padding(
                start = MaterialTheme.dimens.medium1,
                bottom = MaterialTheme.dimens.medium1
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.medium1),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemText(text = leaveInfo.reqDate)

            ItemText(text = leaveInfo.leaveType)

            ItemText(text = leaveInfo.status)

            ItemText(text = leaveInfo.pendingEnd)

            ItemText(text = leaveInfo.totalDays)

            ItemText(text = leaveInfo.fromDate)

            ItemText(text = leaveInfo.toDate)
        }
    }
}

@Composable
private fun ItemText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        modifier = modifier
            .widthIn(min = 95.dp),
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        textAlign = TextAlign.Center
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun Preview() {
    val data = listOf(
        LeaveInfo(
            reqDate = LocalDate.now().toString(),
            fromDate = LocalDate.now().toString(),
            toDate = LocalDate.now().toString(),
            status = "Pending",
            leaveType = "Casual Leave",
            pendingEnd = "Department In-Change",
            totalDays = "2"
        ),
        LeaveInfo(
            reqDate = LocalDate.now().toString(),
            fromDate = LocalDate.now().toString(),
            toDate = LocalDate.now().toString(),
            status = "Pending",
            leaveType = "Casual Leave",
            pendingEnd = "Department In-Change",
            totalDays = "2"
        ),
        LeaveInfo(
            reqDate = LocalDate.now().toString(),
            fromDate = LocalDate.now().toString(),
            toDate = LocalDate.now().toString(),
            status = "Pending",
            leaveType = "Casual Leave",
            pendingEnd = "Department In-Change",
            totalDays = "2"
        ),
        LeaveInfo(
            reqDate = LocalDate.now().toString(),
            fromDate = LocalDate.now().toString(),
            toDate = LocalDate.now().toString(),
            status = "Pending",
            leaveType = "Casual Leave",
            pendingEnd = "Department In-Change",
            totalDays = "2"
        ),
        LeaveInfo(
            reqDate = LocalDate.now().toString(),
            fromDate = LocalDate.now().toString(),
            toDate = LocalDate.now().toString(),
            status = "Pending",
            leaveType = "Casual Leave",
            pendingEnd = "Department In-Change",
            totalDays = "2"
        ),
        LeaveInfo(
            reqDate = LocalDate.now().toString(),
            fromDate = LocalDate.now().toString(),
            toDate = LocalDate.now().toString(),
            status = "Pending",
            leaveType = "Casual Leave",
            pendingEnd = "Department In-Change",
            totalDays = "2"
        )
    )

    TestThem {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onBackground
                        )
                    )
                )
                .padding(top = MaterialTheme.dimens.medium1)
                .horizontalScroll(rememberScrollState())
        ) {
            LeaveHistoryHeaderCard(
                header = listOf(
                    "Request Date",
                    "Leave Type",
                    "Status",
                    "Pending End",
                    "Total Days",
                    "From Date",
                    "To Date",
                )
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

            data.forEach {
                LeaveHistoryItemCard(
                    modifier = Modifier,
                    leaveInfo = it
                )
            }
        }
    }
}
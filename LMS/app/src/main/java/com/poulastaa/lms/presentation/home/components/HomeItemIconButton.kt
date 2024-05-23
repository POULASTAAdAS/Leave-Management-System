package com.poulastaa.lms.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.R
import com.poulastaa.lms.ui.theme.ApplyLeaveIcon
import com.poulastaa.lms.ui.theme.LeaveHistoryIcon
import com.poulastaa.lms.ui.theme.LeaveStatusIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun HomeItemIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize
) {
    Column(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxHeight(.75f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Icon(
                modifier = Modifier
                    .sizeIn(minHeight = 65.dp, minWidth = 65.dp)
                    .fillMaxSize()
                    .padding(MaterialTheme.dimens.medium1),
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = label,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.background,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TestThem {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.large1)
        ) {
            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f),
                icon = ApplyLeaveIcon,
                label = stringResource(id = R.string.apply_leave)
            )

            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f),
                icon = LeaveStatusIcon,
                label = stringResource(id = R.string.leave_status)
            )

            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f),
                icon = LeaveHistoryIcon,
                label = stringResource(id = R.string.leave_history),
            )
        }
    }
}
package com.poulastaa.lms.presentation.leave_history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun LeaveHistoryHeaderCard(
    modifier: Modifier = Modifier,
    header: List<String>
) {
    Card(
        modifier = Modifier.padding(start = MaterialTheme.dimens.medium1),
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
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(header.size) { index ->
                Text(
                    text = header[index],
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.widthIn(min = 90.dp),
                    textAlign = TextAlign.Center
                )

                if (header.size != index + 1)
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.dimens.medium3)
                            .width(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer
                            ),
                    )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        LeaveHistoryHeaderCard(
            header = listOf("Date", "From", "To", "Reason")
        )
    }
}
package com.poulastaa.lms.presentation.home.sact.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.poulastaa.lms.R


@Composable
fun ColumnScope.HeadLine() {
    HeadlinePart(
        modifier = Modifier.align(Alignment.Start),
        text = stringResource(id = R.string.home_headline_part_1)
    )

    HeadlinePart(
        modifier = Modifier.align(Alignment.Start),
        text = stringResource(id = R.string.home_headline_part_2)
    )

    HeadlinePart(
        modifier = Modifier.align(Alignment.Start),
        text = stringResource(id = R.string.home_headline_part_3)
    )
}

@Composable
private fun HeadlinePart(
    modifier: Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
        color = MaterialTheme.colorScheme.background,
        letterSpacing = 2.sp
    )
}
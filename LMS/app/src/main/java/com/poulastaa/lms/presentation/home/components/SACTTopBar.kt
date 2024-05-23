package com.poulastaa.lms.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.ui.theme.AppLogo
import com.poulastaa.lms.ui.theme.TestThem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SACTTopBar(
    modifier: Modifier = Modifier,
    title: String
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )

                Image(
                    modifier = Modifier.size(90.dp),
                    painter = AppLogo,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primaryContainer)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        SACTTopBar(
            title = "Night Owl"
        )
    }
}
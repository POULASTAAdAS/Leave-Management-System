package com.poulastaa.lms.presentation.store_details.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.ui.theme.CheckIcon
import com.poulastaa.lms.ui.theme.TestThem

@Composable
fun StoreDetailsFloatingActionButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    oncCLick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = oncCLick
    ) {
        CircularProgressIndicator(
            strokeWidth = 1.5.dp,
            color = MaterialTheme.colorScheme.primary,
            strokeCap = StrokeCap.Round,
            modifier = Modifier
                .alpha(if (isLoading) 1f else 0f)
                .size(25.dp)
        )

        Icon(
            modifier = Modifier.alpha(if (isLoading) 0f else 1f),
            imageVector = CheckIcon,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        StoreDetailsFloatingActionButton(
            isLoading = true
        ) {

        }
    }
}
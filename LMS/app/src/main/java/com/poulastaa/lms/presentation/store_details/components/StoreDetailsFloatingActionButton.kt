package com.poulastaa.lms.presentation.store_details.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.poulastaa.lms.ui.theme.CheckIcon
import com.poulastaa.lms.ui.theme.TestThem

@Composable
fun StoreDetailsFloatingActionButton(
    modifier: Modifier = Modifier,
    oncCLick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = oncCLick
    ) {
        Icon(
            imageVector = CheckIcon,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        StoreDetailsFloatingActionButton {

        }
    }
}
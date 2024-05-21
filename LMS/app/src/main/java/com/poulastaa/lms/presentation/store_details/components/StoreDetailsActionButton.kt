package com.poulastaa.lms.presentation.store_details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poulastaa.lms.R
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun StoreDetailsActionButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(100f),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 3.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        ),
        enabled = !isLoading
    ) {
        Box(
            modifier = modifier.padding(MaterialTheme.dimens.small2),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(42.dp)
                    .alpha(if (isLoading) 1f else 0f),
                strokeWidth = 3.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            Text(
                modifier = Modifier
                    .alpha(if (isLoading) 0f else 1f),
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                letterSpacing = 1.sp

            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {

        val temp = remember {
            mutableStateOf(false)
        }

        StoreDetailsActionButton(
            isLoading = temp.value,
            text = stringResource(id = R.string.continue_text)
        ) {
            temp.value = !temp.value
        }
    }
}
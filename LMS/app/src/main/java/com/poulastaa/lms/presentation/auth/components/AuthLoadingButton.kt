package com.poulastaa.lms.presentation.auth.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
fun AuthLoadingButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean,
    isEnable: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(100f),
        enabled = isEnable,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = MaterialTheme.colorScheme.onSecondary.copy(.3f),
            disabledContentColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = modifier.padding(MaterialTheme.dimens.small1),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .alpha(if (isLoading) 1f else 0f),
                strokeWidth = 2.dp,
                strokeCap = StrokeCap.Round
            )
            Text(
                modifier = Modifier
                    .alpha(if (isLoading) 0f else 1f),
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        AuthLoadingButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.continue_text),
            isLoading = false,
            onClick = {}
        )
    }
}
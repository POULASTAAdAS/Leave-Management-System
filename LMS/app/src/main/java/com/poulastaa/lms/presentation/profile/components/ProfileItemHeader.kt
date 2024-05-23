package com.poulastaa.lms.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun ProfileItemHeader(
    modifier: Modifier = Modifier,
    label: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.small3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(
            modifier = Modifier
                .height(25.dp)
                .width(6.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(100f)
                )
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.primaryContainer
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TestThem {
        ProfileItemHeader(label = "Details")
    }
}
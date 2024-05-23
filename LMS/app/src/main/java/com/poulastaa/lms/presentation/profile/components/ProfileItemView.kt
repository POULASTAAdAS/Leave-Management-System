package com.poulastaa.lms.presentation.profile.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun ProfileItemView(
    modifier: Modifier = Modifier,
    label: String,
    text: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.small3),
    ) {

        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            color = MaterialTheme.colorScheme.primaryContainer,
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.titleSmall.fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TestThem {
        ProfileItemView(
            label = "Department",
            text = "Computer Science"
        )
    }
}
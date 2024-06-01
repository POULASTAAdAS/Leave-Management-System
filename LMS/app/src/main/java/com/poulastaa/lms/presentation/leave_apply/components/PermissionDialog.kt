package com.poulastaa.lms.presentation.leave_apply.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun PermissionDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.background)
                .padding(MaterialTheme.dimens.medium1),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )

            Text(
                text = description,
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )

            Spacer(modifier = Modifier.heightIn(MaterialTheme.dimens.small1))

            Button(
                onClick = onConfirm, modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.okay),
                    modifier = Modifier.padding(MaterialTheme.dimens.small2),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        ScreenWrapper {
            PermissionDialog(
                title = stringResource(id = R.string.storage_permission_required),
                description = stringResource(id = R.string.storage_permission_desc),
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}
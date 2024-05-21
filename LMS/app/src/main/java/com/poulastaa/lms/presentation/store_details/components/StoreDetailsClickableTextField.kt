package com.poulastaa.lms.presentation.store_details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.poulastaa.lms.ui.theme.CakeIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun StoreDetailsClickableTextField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    trailingIcon: ImageVector? = null,
    color: TextFieldColors,
    isErr: Boolean,
    errStr: String,
    otherColor: Color = MaterialTheme.colorScheme.background,
    disabledContainerColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        value = text,
        onValueChange = {},
        singleLine = true,
        colors = color.copy(
            disabledTextColor = otherColor,
            disabledLabelColor = otherColor,
            disabledIndicatorColor = otherColor,
            disabledContainerColor = disabledContainerColor
        ),
        trailingIcon = {
            if (trailingIcon != null) Icon(
                imageVector = trailingIcon,
                contentDescription = null
            )
        },
        shape = MaterialTheme.shapes.medium,
        label = {
            Text(text = label, overflow = TextOverflow.Ellipsis, softWrap = true)
        },
        isError = isErr,
        supportingText = {
            if (isErr) Text(text = errStr)
        },
        readOnly = true,
        enabled = false
    )
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        Surface(
            color = MaterialTheme.colorScheme.onBackground
        ) {
            StoreDetailsClickableTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.dimens.medium1),
                text = "",
                label = "DBO",
                onClick = {},
                isErr = false,
                errStr = "",
                trailingIcon = CakeIcon,
                color = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.background,
                    unfocusedTextColor = MaterialTheme.colorScheme.background,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.background,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedLabelColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.background,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    disabledTextColor = MaterialTheme.colorScheme.background,
                    disabledLabelColor = MaterialTheme.colorScheme.background,
                    disabledIndicatorColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = Color.Transparent
                )
            )
        }
    }
}
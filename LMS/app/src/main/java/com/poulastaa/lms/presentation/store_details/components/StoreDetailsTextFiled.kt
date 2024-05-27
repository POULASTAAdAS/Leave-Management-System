package com.poulastaa.lms.presentation.store_details.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.poulastaa.lms.ui.theme.EmailIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun StoreDetailsTextFiled(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    onValueChange: (String) -> Unit,
    trailingIcon: ImageVector? = null,
    keyboardType: KeyboardType,
    isErr: Boolean,
    errText: String,
    singleLine: Boolean = true,
    onDone: () -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.medium,
        trailingIcon = {
            trailingIcon?.let {
                Icon(imageVector = trailingIcon, contentDescription = null)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                onDone()
            }
        ),
        singleLine = singleLine,
        label = {
            Text(text = label)
        },
        isError = isErr,
        supportingText = {
            if (isErr) Text(text = errText)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.background,
            unfocusedTextColor = MaterialTheme.colorScheme.background,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.background,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.background,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
            focusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedLabelColor = MaterialTheme.colorScheme.background,
            cursorColor = MaterialTheme.colorScheme.background,
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            errorTextColor = MaterialTheme.colorScheme.error
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TestThem {
        StoreDetailsTextFiled(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.large1),
            text = "",
            label = "Email",
            onValueChange = {},
            trailingIcon = EmailIcon,
            isErr = false,
            errText = "Error",
            keyboardType = KeyboardType.Email
        ) {

        }
    }
}
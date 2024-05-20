package com.poulastaa.lms.presentation.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.poulastaa.lms.R
import com.poulastaa.lms.ui.theme.CheckIcon
import com.poulastaa.lms.ui.theme.EmailIcon
import com.poulastaa.lms.ui.theme.TestThem

@Composable
fun AuthTextFiled(
    modifier: Modifier = Modifier,
    text: String,
    isValidEmail: Boolean,
    isError: Boolean,
    errText: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        value = text,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(id = R.string.email))
        },
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError) Text(text = errText) else Unit
        },
        trailingIcon = {
            if (isValidEmail) Icon(
                imageVector = CheckIcon,
                contentDescription = null
            ) else Unit
        },
        leadingIcon = {
            Icon(
                imageVector = EmailIcon,
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDone()
            }
        ),
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
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
        ) {
            AuthTextFiled(
                modifier = Modifier.fillMaxWidth(),
                text = "poulastaadas2@gmail.com",
                isValidEmail = true,
                isError = true,
                errText = "",
                onValueChange = {}
            ) {

            }
        }
    }
}
package com.poulastaa.lms.presentation.add_teacher

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.store_details.Holder
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsTextFiled
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.CheckIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun AddTeacherRootScreen(
    viewModel: AddTeacherViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is AddTeacherUiAction.EmitToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    AddTeacherScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun AddTeacherScreen(
    state: AddTeacherUiState,
    onEvent: (AddTeacherUiEvent) -> Unit,
    navigateBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    ScreenWrapper {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = navigateBack,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    imageVector = ArrowBackIcon,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

            Text(
                text = stringResource(id = R.string.add_teacher),
                color = MaterialTheme.colorScheme.primaryContainer,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.email.data,
            label = stringResource(id = R.string.email),
            onValueChange = {
                onEvent(AddTeacherUiEvent.OnEmailChange(it))
            },
            trailingIcon = if (state.isValidEmail) CheckIcon else null,
            keyboardType = KeyboardType.Email,
            isErr = state.email.isErr,
            errText = state.email.errText.asString(),
            onDone = {
                focusManager.clearFocus()
                onEvent(AddTeacherUiEvent.OnConformClick)
            }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

        Button(
            onClick = {
                onEvent(AddTeacherUiEvent.OnConformClick)
            },
            modifier = Modifier,
            enabled = state.isValidEmail,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onBackground,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(.7f),
                disabledContentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Box {

                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .alpha(if (state.isMakingApiCall) 1f else 0f),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    strokeWidth = 3.dp
                )

                Text(
                    text = stringResource(id = R.string.continue_text),
                    fontWeight = FontWeight.Medium,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .padding(MaterialTheme.dimens.small3 + MaterialTheme.dimens.small2)
                        .alpha(if (state.isMakingApiCall) 0f else 1f)
                )
            }
        }


        Spacer(modifier = Modifier.weight(1.7f))
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        AddTeacherScreen(state = AddTeacherUiState(
            isValidEmail = true,
            email = Holder(
                data = "test@gmail.com"
            )
        ), onEvent = {}) {

        }
    }
}
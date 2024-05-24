package com.poulastaa.lms.presentation.profile.edit.details_edit

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsFloatingActionButton
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsListSelector
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsTextFiled
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.EmailIcon
import com.poulastaa.lms.ui.theme.PhoneIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.UserIcon
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun DetailsRootScreen(
    name: String,
    email: String,
    phoneOne: String,
    phoneTwo: String,
    qualification: String,
    viewModel: DetailsEditViewModel,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = name) {
        viewModel.populate(
            name = name,
            email = email,
            phoneOne = phoneOne,
            phoneTwo = phoneTwo,
            qualification = qualification
        )
    }

    ObserveAsEvent(flow = viewModel.uiEvent) { event ->
        when (event) {
            is DetailsEditUiAction.ShowToast -> {
                Toast.makeText(
                    context,
                    event.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    DetailsEditScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun DetailsEditScreen(
    state: DetailsEditUiState,
    onEvent: (DetailsEditUiEvent) -> Unit,
    navigateBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    ScreenWrapper(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
        floatingActionButton = {
            StoreDetailsFloatingActionButton(
                modifier = Modifier.padding(MaterialTheme.dimens.medium1),
                isLoading = state.isMakingApiCall
            ) {
                onEvent(DetailsEditUiEvent.OnSaveClick)
            }
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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
                text = stringResource(id = R.string.edit_details),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.name.data,
            label = stringResource(id = R.string.username),
            trailingIcon = UserIcon,
            onValueChange = { onEvent(DetailsEditUiEvent.OnNameChange(it)) },
            keyboardType = KeyboardType.Text,
            isErr = state.name.isErr,
            errText = state.name.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.email.data,
            label = stringResource(id = R.string.email),
            trailingIcon = EmailIcon,
            onValueChange = { onEvent(DetailsEditUiEvent.OnEmailChanged(it)) },
            keyboardType = KeyboardType.Email,
            isErr = state.email.isErr,
            errText = state.email.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.phoneOne.data,
            label = stringResource(id = R.string.phone_1),
            trailingIcon = PhoneIcon,
            onValueChange = { onEvent(DetailsEditUiEvent.OnPhoneNumberOneChange(it)) },
            keyboardType = KeyboardType.Phone,
            isErr = state.phoneOne.isErr,
            errText = state.phoneOne.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.phoneTwo.data,
            label = stringResource(id = R.string.phone_2),
            trailingIcon = PhoneIcon,
            onValueChange = { onEvent(DetailsEditUiEvent.OnPhoneNumberTwoChange(it)) },
            keyboardType = KeyboardType.Phone,
            isErr = state.phoneTwo.isErr,
            errText = state.phoneTwo.errText.asString(),
            onDone = {
                focusManager.clearFocus()
            }
        )

        StoreDetailsListSelector(
            modifier = Modifier.fillMaxWidth(.9f),
            label = stringResource(id = R.string.qualification),
            text = state.qualification.selected,
            isOpen = state.qualification.isDialogOpen,
            list = state.qualification.all,

            color = TextFieldDefaults.colors(
                focusedTrailingIconColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.background,
                disabledTrailingIconColor = MaterialTheme.colorScheme.background,
                errorTextColor = MaterialTheme.colorScheme.error,
            ),
            onCancel = {
                onEvent(DetailsEditUiEvent.OnQualificationDropDownClick)
            },
            onToggle = {
                onEvent(DetailsEditUiEvent.OnQualificationDropDownClick)
            },
            onSelected = {
                onEvent(DetailsEditUiEvent.OnQualificationSelected(it))
            }
        )

        Spacer(modifier = Modifier.weight(3f))
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        DetailsEditScreen(state = DetailsEditUiState(), onEvent = {}) {

        }
    }
}
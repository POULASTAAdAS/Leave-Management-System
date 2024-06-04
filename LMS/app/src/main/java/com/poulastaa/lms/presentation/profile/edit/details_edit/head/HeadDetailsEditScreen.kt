package com.poulastaa.lms.presentation.profile.edit.details_edit.head

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.profile.edit.details_edit.DetailsEditUiAction
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsFloatingActionButton
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsTextFiled
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.EmailIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.UserIcon
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun HeadDetailsEditRootScreen(
    viewModel: HeadDetailsEditViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is DetailsEditUiAction.ShowToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    HeadDetailsEditScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun HeadDetailsEditScreen(
    state: HeadDetailsEditUiState,
    onEvent: (HeadDetailsEditUiEvent) -> Unit,
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
                onEvent(HeadDetailsEditUiEvent.OnSaveClick)
            }
        }
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


        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.name.data,
            label = stringResource(id = R.string.username),
            trailingIcon = EmailIcon,
            onValueChange = { onEvent(HeadDetailsEditUiEvent.OnNameChange(it)) },
            keyboardType = KeyboardType.Email,
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
            trailingIcon = UserIcon,
            onValueChange = { onEvent(HeadDetailsEditUiEvent.OnEmailChanged(it)) },
            keyboardType = KeyboardType.Text,
            isErr = state.email.isErr,
            errText = state.email.errText.asString(),
            onDone = {
                focusManager.clearFocus()

            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        HeadDetailsEditScreen(
            state = HeadDetailsEditUiState(),
            onEvent = { },
            navigateBack = { }
        )
    }
}
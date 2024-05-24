package com.poulastaa.lms.presentation.profile.edit.address_edit

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.stoe_details.AddressType
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsFloatingActionButton
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsTextFiled
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun AddressEditRootScreen(
    viewModel: AddressEditViewModel,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) { event ->
        when (event) {
            is AddressEditUiAction.ShowToast -> {
                Toast.makeText(
                    context,
                    event.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    AddressEditScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun AddressEditScreen(
    state: AddressEditUiState,
    onEvent: (AddressEditUiEvent) -> Unit,
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
                onEvent(AddressEditUiEvent.OnSaveClick)
            }
        }
    ) {
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
                text = if (state.type.name == AddressType.PRESENT.name)
                    stringResource(id = R.string.present_address_header)
                else stringResource(id = R.string.home_address_header),
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.houseNumber.data,
            label = stringResource(id = R.string.hour_number),
            onValueChange = { onEvent(AddressEditUiEvent.OnHouseNumberChang(it)) },
            keyboardType = KeyboardType.Text,
            isErr = state.houseNumber.isErr,
            errText = state.houseNumber.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.street.data,
            label = stringResource(id = R.string.street),
            onValueChange = { onEvent(AddressEditUiEvent.OnStreetChang(it)) },
            keyboardType = KeyboardType.Text,
            isErr = state.street.isErr,
            errText = state.street.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.pinCode.data,
            label = stringResource(id = R.string.zip_code),
            onValueChange = { onEvent(AddressEditUiEvent.OnPostalCodeChang(it)) },
            keyboardType = KeyboardType.Text,
            isErr = state.pinCode.isErr,
            errText = state.pinCode.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.city.data,
            label = stringResource(id = R.string.city),
            onValueChange = { onEvent(AddressEditUiEvent.OnCityChang(it)) },
            keyboardType = KeyboardType.Text,
            isErr = state.city.isErr,
            errText = state.city.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.state.data,
            label = stringResource(id = R.string.state),
            onValueChange = { onEvent(AddressEditUiEvent.OnStateChang(it)) },
            keyboardType = KeyboardType.Text,
            isErr = state.state.isErr,
            errText = state.state.errText.asString(),
            onDone = {
                focusManager.clearFocus()

                onEvent(AddressEditUiEvent.OnSaveClick)
            }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

        Button(
            onClick = {
                onEvent(AddressEditUiEvent.OtherAddressSelected)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.otherSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primaryContainer
            ),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Also Update ${
                    if (state.type == AddressType.PRESENT) stringResource(id = R.string.home_address_header) else stringResource(
                        id = R.string.present_address_header
                    )
                }",
                modifier = Modifier.padding(MaterialTheme.dimens.small3),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        AddressEditScreen(state = AddressEditUiState(), onEvent = {}) {

        }
    }
}
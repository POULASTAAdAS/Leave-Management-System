package com.poulastaa.lms.presentation.store_details

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsActionButton
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsClickableTextField
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsDateDialog
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsListSelector
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsTextFiled
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.CakeIcon
import com.poulastaa.lms.ui.theme.CalenderIcon
import com.poulastaa.lms.ui.theme.EmailIcon
import com.poulastaa.lms.ui.theme.PhoneIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.UserIcon
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun StoreDetailsRootScreen(
    viewModel: StoreDetailsViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) { event ->
        when (event) {
            StoreDetailsUiAction.OnSuccess -> navigateToHome()

            is StoreDetailsUiAction.ShowToast -> {
                Toast.makeText(
                    context,
                    event.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    StoreDetailsScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun StoreDetailsScreen(
    state: StoreDetailsUiState,
    onEvent: (StoreDetailsUiEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedTextColor = MaterialTheme.colorScheme.background,
        unfocusedTextColor = MaterialTheme.colorScheme.background,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.background,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.background,
        disabledTrailingIconColor = MaterialTheme.colorScheme.background,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
        focusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
        focusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedLabelColor = MaterialTheme.colorScheme.background,
        cursorColor = MaterialTheme.colorScheme.background,
        errorTextColor = MaterialTheme.colorScheme.error,
    )

    ScreenWrapper(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.dimens.small2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(id = R.string.fill_up_header),
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                color = MaterialTheme.colorScheme.background
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.userName.data,
            label = stringResource(id = R.string.username),
            trailingIcon = UserIcon,
            onValueChange = {
                onEvent(StoreDetailsUiEvent.OnUserNameChange(it))
            },
            keyboardType = KeyboardType.Text,
            isErr = state.userName.isErr,
            errText = state.userName.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.hrmsId.data,
            label = stringResource(id = R.string.hrms),
            onValueChange = {
                onEvent(StoreDetailsUiEvent.OnHRMSIDChange(it))
            },
            keyboardType = KeyboardType.Text,
            isErr = state.hrmsId.isErr,
            errText = state.hrmsId.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.email.data,
            label = stringResource(id = R.string.email),
            trailingIcon = EmailIcon,
            onValueChange = {
                onEvent(StoreDetailsUiEvent.OnEmailChanged(it))
            },
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
            onValueChange = {
                onEvent(StoreDetailsUiEvent.OnPhoneNumberOneChange(it))
            },
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
            onValueChange = {
                onEvent(StoreDetailsUiEvent.OnPhoneNumberTwoChange(it))
            },
            keyboardType = KeyboardType.Phone,
            isErr = state.phoneTwo.isErr,
            errText = state.phoneTwo.errText.asString(),
            onDone = {
                focusManager.clearFocus()
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
        ) {
            StoreDetailsClickableTextField(
                modifier = Modifier.fillMaxWidth(.5f),
                text = state.bDay.data,
                trailingIcon = CakeIcon,
                label = stringResource(id = R.string.date_of_birth),
                color = textFieldColors,
                isErr = state.bDay.isErr,
                errStr = state.bDay.errText.asString(),
                onClick = {
                    onEvent(StoreDetailsUiEvent.OnBDateDialogClick)
                }
            )

            StoreDetailsListSelector(
                modifier = Modifier.fillMaxWidth(.4f),
                label = stringResource(id = R.string.gender),
                text = state.gender.selected,
                isOpen = state.gender.isDialogOpen,
                list = state.gender.all,
                color = textFieldColors,
                onToggle = {
                    onEvent(StoreDetailsUiEvent.OnGenderDropDownClick)
                },
                onCancel = {
                    if (state.gender.isDialogOpen) onEvent(StoreDetailsUiEvent.OnGenderDropDownClick)
                },
                onSelected = {
                    onEvent(StoreDetailsUiEvent.OnGenderSelected(it))
                }
            )
        }


        StoreDetailsListSelector(
            modifier = Modifier.fillMaxWidth(.9f),
            label = stringResource(id = R.string.designation),
            text = state.designation.selected,
            isOpen = state.designation.isDialogOpen,
            list = state.designation.all,
            color = textFieldColors,
            onToggle = {
                onEvent(StoreDetailsUiEvent.OnDesignationDropDownClick)
            },
            onCancel = {
                if (state.designation.isDialogOpen) onEvent(StoreDetailsUiEvent.OnDesignationDropDownClick)
            },
            onSelected = {
                onEvent(StoreDetailsUiEvent.OnDesignationSelected(it))
            }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        StoreDetailsListSelector(
            modifier = Modifier.fillMaxWidth(.9f),
            label = stringResource(id = R.string.department),
            text = state.department.selected,
            isOpen = state.department.isDialogOpen,
            list = state.department.all,
            color = textFieldColors,
            onToggle = {
                onEvent(StoreDetailsUiEvent.OnDepartmentDropDownClick)
            },
            onCancel = {
                if (state.department.isDialogOpen) onEvent(StoreDetailsUiEvent.OnDepartmentDropDownClick)
            },
            onSelected = {
                onEvent(StoreDetailsUiEvent.OnDepartmentSelected(it))
            }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
        ) {
            StoreDetailsClickableTextField(
                modifier = Modifier.fillMaxWidth(.5f),
                text = state.joiningDate.data,
                trailingIcon = CalenderIcon,
                label = stringResource(id = R.string.date_of_joining),
                color = textFieldColors,
                isErr = state.joiningDate.isErr,
                errStr = state.joiningDate.errText.asString(),
                onClick = {
                    onEvent(StoreDetailsUiEvent.OnJoinDateDialogClick)
                }
            )

            StoreDetailsClickableTextField(
                modifier = Modifier.fillMaxWidth(),
                text = state.experience,
                label = stringResource(id = R.string.experience),
                color = textFieldColors,
                otherColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(.1f),
                isErr = false,
                errStr = "",
                onClick = {}
            )
        }

        StoreDetailsListSelector(
            modifier = Modifier.fillMaxWidth(.9f),
            label = stringResource(id = R.string.qualification),
            text = state.qualification.selected,
            isOpen = state.qualification.isDialogOpen,
            list = state.qualification.all,
            color = textFieldColors,
            onToggle = {
                onEvent(StoreDetailsUiEvent.OnQualificationDropDownClick)
            },
            onCancel = {
                if (state.qualification.isDialogOpen) onEvent(StoreDetailsUiEvent.OnQualificationDropDownClick)
            },
            onSelected = {
                onEvent(StoreDetailsUiEvent.OnQualificationSelected(it))
            }
        )


        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))


        PresentAddress(
            presetAddress = state.presentAddress,
            focusManager = focusManager,
            onEvent = onEvent
        )

        HomeAddress(
            homeAddress = state.homeAddress,
            focusManager = focusManager,
            onEvent = onEvent
        )


        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))


        StoreDetailsActionButton(
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isMakingApiCall,
            text = stringResource(id = R.string.continue_text),
            onClick = {
                onEvent(StoreDetailsUiEvent.OnContinueClick)
            }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium3))



        if (state.bDay.isDialogOpen)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                StoreDetailsDateDialog(
                    label = stringResource(id = R.string.bdate),
                    onDismissRequest = {
                        onEvent(StoreDetailsUiEvent.OnBDateDialogClick)
                    },
                    onSuccess = {
                        onEvent(StoreDetailsUiEvent.OnBDateChange(it))
                    }
                )
            }

        if (state.joiningDate.isDialogOpen)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                StoreDetailsDateDialog(
                    label = stringResource(id = R.string.date_of_joining),
                    onDismissRequest = {
                        onEvent(StoreDetailsUiEvent.OnJoinDateDialogClick)
                    },
                    onSuccess = {
                        onEvent(StoreDetailsUiEvent.OnJoinDateChange(it))
                    }
                )
            }
    }
}

@Composable
private fun PresentAddress(
    presetAddress: HolderAddress,
    focusManager: FocusManager,
    onEvent: (StoreDetailsUiEvent) -> Unit
) {
    Text(
        text = AnnotatedString(
            text = stringResource(id = R.string.present_address_header),
            spanStyle = SpanStyle(
                color = MaterialTheme.colorScheme.primaryContainer,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                letterSpacing = 2.sp
            )
        ),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))

    StoreDetailsTextFiled(
        modifier = Modifier.fillMaxWidth(),
        text = presetAddress.houseNumber.data,
        label = stringResource(id = R.string.hour_number),
        onValueChange = {
            onEvent(StoreDetailsUiEvent.PresentAddress.HouseNumberChange(it))
        },
        keyboardType = KeyboardType.Text,
        isErr = presetAddress.houseNumber.isErr,
        errText = presetAddress.houseNumber.errText.asString(),
        onDone = {
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    StoreDetailsTextFiled(
        modifier = Modifier.fillMaxWidth(),
        text = presetAddress.street.data,
        label = stringResource(id = R.string.street),
        onValueChange = {
            onEvent(StoreDetailsUiEvent.PresentAddress.StreetChange(it))
        },
        keyboardType = KeyboardType.Text,
        isErr = presetAddress.street.isErr,
        errText = presetAddress.street.errText.asString(),
        onDone = {
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1)
    ) {
        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(.5f),
            text = presetAddress.city.data,
            label = stringResource(id = R.string.city),
            onValueChange = {
                onEvent(StoreDetailsUiEvent.PresentAddress.CityChange(it))
            },
            keyboardType = KeyboardType.Text,
            isErr = presetAddress.city.isErr,
            errText = presetAddress.city.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = presetAddress.zipCode.data,
            label = stringResource(id = R.string.zip_code),
            onValueChange = {
                onEvent(StoreDetailsUiEvent.PresentAddress.ZipCodeChange(it))
            },
            keyboardType = KeyboardType.Number,
            isErr = presetAddress.zipCode.isErr,
            errText = presetAddress.zipCode.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    }

    StoreDetailsTextFiled(
        modifier = Modifier.fillMaxWidth(),
        text = presetAddress.state.data,
        label = stringResource(id = R.string.state),
        onValueChange = {
            onEvent(StoreDetailsUiEvent.PresentAddress.StateChange(it))
        },
        keyboardType = KeyboardType.Text,
        isErr = presetAddress.state.isErr,
        errText = presetAddress.state.errText.asString(),
        onDone = {
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    StoreDetailsTextFiled(
        modifier = Modifier.fillMaxWidth(),
        text = presetAddress.country.data,
        label = stringResource(id = R.string.country),
        onValueChange = {
            onEvent(StoreDetailsUiEvent.PresentAddress.CountryChange(it))
        },
        keyboardType = KeyboardType.Text,
        isErr = presetAddress.country.isErr,
        errText = presetAddress.country.errText.asString(),
        onDone = {
            focusManager.moveFocus(FocusDirection.Down)
        }
    )
}


@Composable
private fun HomeAddress(
    homeAddress: HolderAddress,
    focusManager: FocusManager,
    onEvent: (StoreDetailsUiEvent) -> Unit
) {
    Text(
        text = AnnotatedString(
            text = stringResource(id = R.string.home_address_header),
            spanStyle = SpanStyle(
                color = MaterialTheme.colorScheme.primaryContainer,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                letterSpacing = 2.sp,
            )
        ),
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))

    Button(
        onClick = { onEvent(StoreDetailsUiEvent.OnSameAsPresentAddressClick) },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            modifier = Modifier.padding(MaterialTheme.dimens.small1),
            text = stringResource(id = R.string.same_as_present_address),
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))

    StoreDetailsTextFiled(
        modifier = Modifier.fillMaxWidth(),
        text = homeAddress.houseNumber.data,
        label = stringResource(id = R.string.hour_number),
        onValueChange = {
            onEvent(StoreDetailsUiEvent.HomeAddress.HouseNumberChange(it))
        },
        keyboardType = KeyboardType.Text,
        isErr = homeAddress.houseNumber.isErr,
        errText = homeAddress.houseNumber.errText.asString(),
        onDone = {
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    StoreDetailsTextFiled(
        modifier = Modifier.fillMaxWidth(),
        text = homeAddress.street.data,
        label = stringResource(id = R.string.street),
        onValueChange = {
            onEvent(StoreDetailsUiEvent.HomeAddress.StreetChange(it))
        },
        keyboardType = KeyboardType.Text,
        isErr = homeAddress.street.isErr,
        errText = homeAddress.street.errText.asString(),
        onDone = {
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1)
    ) {
        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(.5f),
            text = homeAddress.city.data,
            label = stringResource(id = R.string.city),
            onValueChange = {
                onEvent(StoreDetailsUiEvent.HomeAddress.CityChange(it))
            },
            keyboardType = KeyboardType.Text,
            isErr = homeAddress.city.isErr,
            errText = homeAddress.city.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = homeAddress.zipCode.data,
            label = stringResource(id = R.string.zip_code),
            onValueChange = {
                onEvent(StoreDetailsUiEvent.HomeAddress.ZipCodeChange(it))
            },
            keyboardType = KeyboardType.Number,
            isErr = homeAddress.zipCode.isErr,
            errText = homeAddress.zipCode.errText.asString(),
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    }

    StoreDetailsTextFiled(
        modifier = Modifier.fillMaxWidth(),
        text = homeAddress.state.data,
        label = stringResource(id = R.string.state),
        onValueChange = {
            onEvent(StoreDetailsUiEvent.HomeAddress.StateChange(it))
        },
        keyboardType = KeyboardType.Text,
        isErr = homeAddress.state.isErr,
        errText = homeAddress.state.errText.asString(),
        onDone = {
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    StoreDetailsTextFiled(
        modifier = Modifier.fillMaxWidth(),
        text = homeAddress.country.data,
        label = stringResource(id = R.string.country),
        onValueChange = {
            onEvent(StoreDetailsUiEvent.HomeAddress.CountryChange(it))
        },
        keyboardType = KeyboardType.Text,
        isErr = homeAddress.country.isErr,
        errText = homeAddress.country.errText.asString(),
        onDone = {
            focusManager.clearFocus()
        }
    )
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        StoreDetailsScreen(
            state = StoreDetailsUiState(
            )
        ) {

        }
    }
}
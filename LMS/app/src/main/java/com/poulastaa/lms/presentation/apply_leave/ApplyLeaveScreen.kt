package com.poulastaa.lms.presentation.apply_leave

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.store_details.ListHolder
import com.poulastaa.lms.presentation.store_details.components.LMSDateDialog
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsClickableTextField
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsFloatingActionButton
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsListSelector
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsTextFiled
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.CalenderIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent
import com.poulastaa.lms.ui.utils.UiText

@Composable
fun ApplyLeaveRootScreen(
    viewModel: ApplyLeaveViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is ApplyLeaveUiAction.OnSuccess -> {
                Toast.makeText(
                    context,
                    UiText.StringResource(R.string.apply_leave_success).asString(context),
                    Toast.LENGTH_SHORT
                ).show()

                navigateBack()
            }

            is ApplyLeaveUiAction.Err -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    ApplyLeaveScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun ApplyLeaveScreen(
    state: ApplyLeaveUiState,
    onEvent: (ApplyLeaveUiEvent) -> Unit,
    navigateBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    val textFieldColors = TextFieldDefaults.colors(
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


    ScreenWrapper(
        floatingActionButton = {
            StoreDetailsFloatingActionButton(
                modifier = Modifier.padding(MaterialTheme.dimens.medium1),
                isLoading = state.isMakingApiCall,
                oncCLick = {
                    onEvent(ApplyLeaveUiEvent.OnReqClick)
                }
            )
        },
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1)
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
                text = stringResource(id = R.string.apply_leave),
                color = MaterialTheme.colorScheme.primaryContainer,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
            )
        }

        StoreDetailsListSelector(
            modifier = Modifier.fillMaxWidth(.9f),
            label = stringResource(id = R.string.leave_type),
            text = state.leaveType.selected,
            isOpen = state.leaveType.isDialogOpen,
            list = state.leaveType.all,
            color = textFieldColors,
            onCancel = {
                onEvent(ApplyLeaveUiEvent.OnLeaveTypeToggle)
            },
            onToggle = {
                onEvent(ApplyLeaveUiEvent.OnLeaveTypeToggle)
            },
            onSelected = {
                onEvent(ApplyLeaveUiEvent.OnLeaveTypeSelected(it))
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
        ) {
            Box {
                StoreDetailsClickableTextField(
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .alpha(if (state.isGettingLeaveBalance) 0f else 1f),
                    text = state.balance,
                    label = stringResource(id = R.string.leave_balance),
                    color = textFieldColors,
                    otherColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(.1f),
                    isErr = false,
                    errStr = "",
                    onClick = {}
                )

                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .alpha(if (state.isGettingLeaveBalance) 1f else 0f),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 3.dp
                )
            }

            StoreDetailsListSelector(
                modifier = Modifier.fillMaxWidth(.4f),
                label = "",
                text = state.dayType.selected,
                isOpen = state.dayType.isDialogOpen,
                list = state.dayType.all,
                color = textFieldColors,
                onCancel = {
                    onEvent(ApplyLeaveUiEvent.OnDayTypeToggle)
                },
                onToggle = {
                    onEvent(ApplyLeaveUiEvent.OnDayTypeToggle)
                },
                onSelected = {
                    onEvent(ApplyLeaveUiEvent.OnDayTypeSelected(it))
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
        ) {
            StoreDetailsClickableTextField(
                modifier = Modifier.fillMaxWidth(.5f),
                text = state.fromDate.data,
                trailingIcon = CalenderIcon,
                label = stringResource(id = R.string.from_date),
                color = textFieldColors,
                isErr = state.fromDate.isErr,
                errStr = state.fromDate.errText.asString(),
                onClick = {
                    onEvent(ApplyLeaveUiEvent.OnFromDateToggle)
                }
            )

            StoreDetailsClickableTextField(
                modifier = Modifier.fillMaxWidth(),
                text = state.toDate.data,
                trailingIcon = CalenderIcon,
                label = stringResource(id = R.string.to_date),
                color = textFieldColors,
                isErr = state.toDate.isErr,
                errStr = state.toDate.errText.asString(),
                onClick = {
                    onEvent(ApplyLeaveUiEvent.OnToDateToggle)
                }
            )
        }

        StoreDetailsClickableTextField(
            modifier = Modifier.fillMaxWidth(.8f),
            text = state.totalDays,
            label = stringResource(id = R.string.total_days),
            color = textFieldColors,
            otherColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(.1f),
            isErr = false,
            errStr = "",
            onClick = {}
        )


        StoreDetailsTextFiled(
            modifier = Modifier.fillMaxWidth(),
            text = state.leaveReason.data,
            label = stringResource(id = R.string.leave_reason),
            onValueChange = { onEvent(ApplyLeaveUiEvent.OnLeaveReason(it)) },
            keyboardType = KeyboardType.Text,
            isErr = state.leaveReason.isErr,
            errText = state.leaveReason.errText.asString(),
            singleLine = false,
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )

        if (state.addressDuringLeave.selected == "OutStation") {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onEvent(ApplyLeaveUiEvent.OnAddressDuringLeaveOutSideBackClick)
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Icon(
                        imageVector = ArrowBackIcon,
                        contentDescription = null
                    )
                }

                StoreDetailsTextFiled(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.addressDuringLeaveOutStation.data,
                    label = stringResource(id = R.string.outstation),
                    onValueChange = { onEvent(ApplyLeaveUiEvent.OnAddressDuringLeaveOther(it)) },
                    keyboardType = KeyboardType.Text,
                    isErr = state.addressDuringLeaveOutStation.isErr,
                    errText = state.addressDuringLeaveOutStation.errText.asString(),
                    singleLine = false,
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            }
        } else {
            StoreDetailsListSelector(
                modifier = Modifier.fillMaxWidth(.9f),
                label = stringResource(id = R.string.address_during_leave),
                text = state.addressDuringLeave.selected,
                isOpen = state.addressDuringLeave.isDialogOpen,
                list = state.addressDuringLeave.all,
                color = textFieldColors,
                onCancel = {
                    onEvent(ApplyLeaveUiEvent.OnAddressDuringLeaveToggle)
                },
                onToggle = {
                    onEvent(ApplyLeaveUiEvent.OnAddressDuringLeaveToggle)
                },
                onSelected = {
                    onEvent(ApplyLeaveUiEvent.OnAddressDuringLeaveSelected(it))
                }
            )
        }

        StoreDetailsListSelector(
            modifier = Modifier.fillMaxWidth(.9f),
            label = stringResource(id = R.string.leave_path),
            text = state.path.selected,
            isOpen = state.path.isDialogOpen,
            list = state.path.all,
            color = textFieldColors,
            onCancel = {
                onEvent(ApplyLeaveUiEvent.OnPathToggle)
            },
            onToggle = {
                onEvent(ApplyLeaveUiEvent.OnPathToggle)
            },
            onSelected = {
                onEvent(ApplyLeaveUiEvent.OnPathSelected(it))
            }
        )

        if (state.fromDate.isDialogOpen) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                LMSDateDialog(
                    label = stringResource(id = R.string.select_from_date),
                    onDismissRequest = {
                        onEvent(ApplyLeaveUiEvent.OnFromDateToggle)
                    },
                    onSuccess = {
                        onEvent(ApplyLeaveUiEvent.OnFromDateSelected(it))
                    }
                )
        }

        if (state.toDate.isDialogOpen) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                LMSDateDialog(
                    label = stringResource(id = R.string.select_to_date),
                    onDismissRequest = {
                        onEvent(ApplyLeaveUiEvent.OnToDateToggle)
                    },
                    onSuccess = {
                        onEvent(ApplyLeaveUiEvent.OnToDateSelected(it))
                    }
                )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        ApplyLeaveScreen(
            state = ApplyLeaveUiState(
                isGettingLeaveBalance = false,
                addressDuringLeave = ListHolder(
                    selected = ""
                )
            ),
            onEvent = {},
            navigateBack = {}
        )
    }
}
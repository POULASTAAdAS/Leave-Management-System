package com.poulastaa.lms.presentation.leave_status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsClickableTextField
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsListSelector
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun LeaveStatusRootScreen(
    viewModel: LeaveStatusViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    LeaveStatusScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}


@Composable
private fun LeaveStatusScreen(
    state: LeaveStatusUiState,
    onEvent: (LeaveStatusUiEvent) -> Unit,
    navigateBack: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
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
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = navigateBack, colors = IconButtonDefaults.iconButtonColors(
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
                text = stringResource(id = R.string.leave_status_header),
                color = MaterialTheme.colorScheme.primaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        StoreDetailsListSelector(
            modifier = Modifier.fillMaxWidth(.9f),
            label = stringResource(id = R.string.leave_type),
            text = state.leaveTypes.selected,
            isOpen = state.leaveTypes.isDialogOpen,
            list = state.leaveTypes.all,
            color = textFieldColors,
            onCancel = {
                onEvent(LeaveStatusUiEvent.OnLeaveTypeToggle)
            },
            onToggle = {
                onEvent(LeaveStatusUiEvent.OnLeaveTypeToggle)
            },
            onSelected = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onEvent(LeaveStatusUiEvent.OnLeaveTypeSelected(it))
            }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        Row(
            modifier = Modifier.fillMaxWidth(.8f),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                StoreDetailsClickableTextField(
                    modifier = Modifier.alpha(if (state.isMakingApiCall) 0f else 1f),
                    text = state.balance,
                    label = stringResource(id = R.string.leave_balance),
                    color = textFieldColors,
                    isErr = false,
                    errStr = "",
                    onClick = {},
                    disabledContainerColor = MaterialTheme.colorScheme.background.copy(.1f)
                )

                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    strokeWidth = 3.dp,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .size(40.dp)
                        .alpha(if (state.isMakingApiCall) 1f else 0f),
                )
            }
        }
    }
}


@Preview
@Composable
private fun Preview() {
    TestThem {
        LeaveStatusScreen(state = LeaveStatusUiState(
            isMakingApiCall = false
        ), onEvent = {}) {

        }
    }
}
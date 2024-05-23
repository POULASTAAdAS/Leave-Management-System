package com.poulastaa.lms.presentation.home.permenent

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.LocalUser
import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.presentation.home.HomeUiAction
import com.poulastaa.lms.presentation.home.components.HomeItemIconButton
import com.poulastaa.lms.presentation.utils.HomeWrapperWithAppBar
import com.poulastaa.lms.ui.theme.ApplyLeaveIcon
import com.poulastaa.lms.ui.theme.ApproveLeaveIcon
import com.poulastaa.lms.ui.theme.LeaveHistoryIcon
import com.poulastaa.lms.ui.theme.LeaveReportIcon
import com.poulastaa.lms.ui.theme.LeaveStatusIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.ViewLeaveIcon
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun HomePermanentRootScreen(
    time: String,
    user: LocalUser,
    viewModel: HomePermanentViewModel = hiltViewModel(),
    navigate: (Screens) -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is HomeUiAction.OnNavigate -> navigate(it.screens)

            is HomeUiAction.ShowToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    HomePermanentScreen(
        state = viewModel.state,
        time = time,
        user = user,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HomePermanentScreen(
    state: HomePermanentUiState,
    time: String,
    user: LocalUser,
    onEvent: (HomePermanentUiEvent) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    HomeWrapperWithAppBar(
        time = time,
        user = user,
        onEvent = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onEvent(HomePermanentUiEvent.OnProfilePicClick)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.large1)
        ) {
            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePermanentUiEvent.OnApplyLeaveClick)
                        }
                    ),
                icon = ApplyLeaveIcon,
                label = stringResource(id = R.string.apply_leave)
            )

            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePermanentUiEvent.OnLeaveStatusClick)
                        }
                    ),
                icon = LeaveStatusIcon,
                label = stringResource(id = R.string.leave_status)
            )

            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePermanentUiEvent.OnLeaveHistoryClick)
                        }
                    ),
                icon = LeaveHistoryIcon,
                label = stringResource(id = R.string.leave_history),
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.large1)
        ) {
            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePermanentUiEvent.OnApproveLeaveClick)
                        }
                    ),
                icon = ApproveLeaveIcon,
                label = stringResource(id = R.string.approve_leave)
            )

            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePermanentUiEvent.OnViewLeaveClick)
                        }
                    ),
                icon = ViewLeaveIcon,
                label = stringResource(id = R.string.view)
            )

            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePermanentUiEvent.OnViewReportClick)
                        }
                    ),
                icon = LeaveReportIcon,
                label = stringResource(id = R.string.report),
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        HomePermanentScreen(
            state = HomePermanentUiState(),
            time = "Good Morning",
            user = LocalUser(
                name = "Poulastaa Das",
                designation = "Assistant Professor",
                department = "Computer Science",
                isDepartmentInCharge = true
            )
        ) {

        }
    }
}
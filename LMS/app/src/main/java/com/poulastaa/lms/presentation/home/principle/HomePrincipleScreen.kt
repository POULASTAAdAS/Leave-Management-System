package com.poulastaa.lms.presentation.home.principle

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
import com.poulastaa.lms.ui.theme.AddIcon
import com.poulastaa.lms.ui.theme.ApplyLeaveIcon
import com.poulastaa.lms.ui.theme.ApproveLeaveIcon
import com.poulastaa.lms.ui.theme.DepartmentInChargeIcon
import com.poulastaa.lms.ui.theme.LeaveHistoryIcon
import com.poulastaa.lms.ui.theme.LeaveReportIcon
import com.poulastaa.lms.ui.theme.LeaveStatusIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.ViewLeaveIcon
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun HomePrincipleRootScreen(
    time: String,
    user: LocalUser,
    viewModel: HomePrincipleViewModel = hiltViewModel(),
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

    HomePrincipleScreen(
        state = viewModel.state,
        time = time,
        user = user,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun HomePrincipleScreen(
    state: HomePrincipleUiState,
    time: String,
    user: LocalUser,
    onEvent: (HomePrincipleUiEvent) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    HomeWrapperWithAppBar(
        time = time,
        user = user,
        isPrincipal = true,
        onEvent = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onEvent(HomePrincipleUiEvent.OnProfilePicClick)
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
                            onEvent(HomePrincipleUiEvent.OnApplyLeaveClick)
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
                            onEvent(HomePrincipleUiEvent.OnLeaveStatusClick)
                        }
                    ),
                icon = LeaveStatusIcon,
                label = stringResource(id = R.string.leave_status),
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )

            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePrincipleUiEvent.OnLeaveHistoryClick)
                        }
                    ),
                icon = LeaveHistoryIcon,
                label = stringResource(id = R.string.leave_history),
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

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
                            onEvent(HomePrincipleUiEvent.OnDefineDepartmentInChargeClick)
                        }
                    ),
                icon = DepartmentInChargeIcon,
                label = stringResource(id = R.string.define_department_incharge),
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )



            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePrincipleUiEvent.OnApproveLeaveClick)
                        }
                    ),
                icon = ApproveLeaveIcon,
                label = stringResource(id = R.string.approve_leave),
            )

            HomeItemIconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePrincipleUiEvent.OnAddClick)
                        }
                    ),
                icon = AddIcon,
                label = stringResource(id = R.string.add)
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.large1)
        ) {
            HomeItemIconButton(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePrincipleUiEvent.OnViewLeaveClick)
                        }
                    ),
                icon = ViewLeaveIcon,
                label = stringResource(id = R.string.view)
            )

            HomeItemIconButton(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HomePrincipleUiEvent.OnViewReportClick)
                        }
                    ),
                icon = LeaveReportIcon,
                label = stringResource(id = R.string.report),
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        HomePrincipleScreen(
            state = HomePrincipleUiState(),
            time = "Good Morning",
            user = LocalUser()
        ) {

        }
    }
}
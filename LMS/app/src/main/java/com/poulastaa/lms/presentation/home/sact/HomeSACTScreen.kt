package com.poulastaa.lms.presentation.home.sact

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.poulastaa.lms.ui.theme.LeaveHistoryIcon
import com.poulastaa.lms.ui.theme.LeaveStatusIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun HomeSACTRoot(
    time: String,
    user: LocalUser,
    cookie: String,
    viewModel: HomeSACTViewModel = hiltViewModel(),
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

    HomeSACTScreen(
        time = time,
        user = user,
        cookie = cookie,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HomeSACTScreen(
    time: String,
    user: LocalUser,
    cookie: String,
    onEvent: (HomeSACTUiEvent) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    HomeWrapperWithAppBar(
        time = time,
        user = user,
        cookie = cookie,
        onEvent = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

            onEvent(HomeSACTUiEvent.OnProfilePicClick)
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
                            onEvent(HomeSACTUiEvent.OnApplyLeaveClick)
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
                            onEvent(HomeSACTUiEvent.OnLeaveStatusClick)
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
                            onEvent(HomeSACTUiEvent.OnLeaveHistoryClick)
                        }
                    ),
                icon = LeaveHistoryIcon,
                label = stringResource(id = R.string.leave_history),
            )
        }
    }
}


@Preview
@Composable
private fun Preview() {
    TestThem {
        HomeSACTScreen(
            time = "Night Owl",
            cookie = "",
            user = LocalUser(
                name = "Poulastaa Das",
                designation = "SACT-I",
                department = "Computer Science"
            )
        ) {

        }
    }
}
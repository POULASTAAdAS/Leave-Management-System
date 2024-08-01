package com.poulastaa.lms.presentation.home.head_clark

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.poulastaa.lms.presentation.home.components.HomeItemIconButton
import com.poulastaa.lms.presentation.utils.HomeWrapperWithAppBar
import com.poulastaa.lms.ui.theme.ApproveLeaveIcon
import com.poulastaa.lms.ui.theme.LeaveReportIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.ViewLeaveIcon
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun HeadClarkRootScreen(
    time: String,
    user: LocalUser,
    cookie: String,
    viewModel: HeadClarkViewModel = hiltViewModel(),
    navigate: (Screens) -> Unit,
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is HeadClarkUiAction.OnNavigate -> navigate(it.screens)

            is HeadClarkUiAction.ShowToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    HeadClarkScreen(
        time = time,
        user = user,
        cookie = cookie,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HeadClarkScreen(
    time: String,
    user: LocalUser,
    cookie: String,
    onEvent: (HeadClarkUiEvent) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current


    HomeWrapperWithAppBar(
        time = time,
        user = user,
        isHeadClark = true,
        cookie = cookie,
        onEvent = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onEvent(HeadClarkUiEvent.OnProfileClick)
        }
    ) {
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
                            onEvent(HeadClarkUiEvent.OnApproveLeaveClick)
                        }
                    ),
                icon = ApproveLeaveIcon,
                label = stringResource(id = R.string.approve_leave),
            )

            HomeItemIconButton(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEvent(HeadClarkUiEvent.OnViewLeaveClick)
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
                            onEvent(HeadClarkUiEvent.OnDownloadReportClick)
                        }
                    ),
                icon = LeaveReportIcon,
                label = stringResource(id = R.string.download_report)
            )
        }
    }
}


@Preview
@Composable
private fun Preview() {
    TestThem {
        HeadClarkScreen(
            time = "",
            user = LocalUser(
                designation = "Head Clark"
            ),
            cookie = ""
        ) {

        }
    }
}
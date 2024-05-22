package com.poulastaa.lms.presentation.home.sact

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.data.model.auth.LocalUser
import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.presentation.home.components.HomeItemIconButton
import com.poulastaa.lms.presentation.home.sact.components.HeadLine
import com.poulastaa.lms.presentation.home.sact.components.Profile
import com.poulastaa.lms.presentation.home.sact.components.SACTTopBar
import com.poulastaa.lms.presentation.utils.ScreenWrapperWithAppBar
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
    viewModel: HomeSACTViewModel = hiltViewModel(),
    navigate: (Screens) -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is HomeSACTUiAction.OnNavigate -> navigate(it.screens)

            is HomeSACTUiAction.ShowToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    HomeSACTScreen(
        state = viewModel.state,
        time = time,
        user = user,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HomeSACTScreen(
    state: HomeSACTUiState,
    time: String,
    user: LocalUser,
    onEvent: (HomeSACTUiEvent) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    ScreenWrapperWithAppBar(
        topBar = {
            SACTTopBar(title = time)
        },
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        HeadLine()

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Profile(
                url = user.profilePicUrl,
                sex = user.sex,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onEvent(HomeSACTUiEvent.OnProfilePicClick)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = MaterialTheme.dimens.medium1),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = user.name,
                    color = MaterialTheme.colorScheme.background,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${user.designation} (${user.department})",
                    color = MaterialTheme.colorScheme.background,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
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
            state = HomeSACTUiState(), time = "Night Owl", user = LocalUser(
                name = "Poulastaa Das",
                designation = "SACT-I",
                department = "Computer Science"
            )
        ) {

        }
    }
}
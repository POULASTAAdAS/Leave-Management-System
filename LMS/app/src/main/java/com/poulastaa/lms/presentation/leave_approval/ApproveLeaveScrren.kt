package com.poulastaa.lms.presentation.leave_approval

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun ApproveLeaveRootScreen(
    viewModel: ApproveLeaveViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit, key2 = viewModel.state.data.isEmpty()) {
        viewModel.getInitialValue()
    }

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is ApproveLeaveUiAction.EmitToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    ApproveLeaveScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun ApproveLeaveScreen(
    state: ApproveLeaveUiState,
    onEvent: (ApproveLeaveUiEvent) -> Unit,
    navigateBack: () -> Unit
) {
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
                text = stringResource(id = R.string.approve_leave),
                color = MaterialTheme.colorScheme.primaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
            )
        }


    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        ApproveLeaveScreen(state = ApproveLeaveUiState(), onEvent = {}) {

        }
    }
}
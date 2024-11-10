package com.poulastaa.lms.presentation.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.presentation.auth.components.AuthLoadingButton
import com.poulastaa.lms.presentation.auth.components.AuthTextButton
import com.poulastaa.lms.presentation.auth.components.AuthTextFiled
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.AppLogo
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun AuthRootScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navigate: (Screens) -> Unit
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) { event ->
        when (event) {
            is AuthUiAction.OnSuccess -> {
                navigate(event.route)
            }

            is AuthUiAction.SendToast -> {
                Toast.makeText(
                    context,
                    event.value.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    AuthScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun AuthScreen(
    state: AuthUiState,
    onEvent: (AuthUiEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current

    ScreenWrapper {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = AppLogo,
                contentDescription = null
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.lm),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.s),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

            AuthTextFiled(
                modifier = Modifier.fillMaxWidth(),
                text = state.email,
                isValidEmail = state.isValidEmail,
                isError = state.isEmailErr,
                errText = state.emailErr.asString(),
                onValueChange = {
                    onEvent(AuthUiEvent.EmailChanged(it))
                },
                onDone = {
                    focusManager.clearFocus()
                    onEvent(AuthUiEvent.OnContinueClick)
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

            AuthLoadingButton(
                modifier = Modifier.fillMaxWidth(.8f),
                text = stringResource(id = R.string.continue_text),
                isEnable = state.isValidEmail,
                isLoading = state.isMakingApiCall,
                onClick = { onEvent(AuthUiEvent.OnContinueClick) }
            )
        }


        Column(
            modifier = Modifier
                .weight(.6f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = state.isEmailHintVisible) {
                Column {
                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

                    val temp = remember {
                        state.isEmailHintVisible
                    }

                    if (temp) Text(
                        text = stringResource(id = R.string.email_hint),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.background,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )
                }
            }

            AnimatedVisibility(
                modifier = Modifier.fillMaxSize(),
                visible = state.resendVerificationEmailVisible
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.send_auth_mail_again),
                        color = MaterialTheme.colorScheme.background,
                        fontWeight = FontWeight.Light,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

                    AuthTextButton(
                        modifier = Modifier.fillMaxWidth(.8f),
                        text = state.resendVerificationEmailButtonText,
                        isEnable = state.resendVerificationEmailButtonEnabled,
                        onClick = { onEvent(AuthUiEvent.OnResendVerificationClick) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        AuthScreen(
            state = AuthUiState(
                resendVerificationEmailVisible = true
            )
        ) {

        }
    }
}
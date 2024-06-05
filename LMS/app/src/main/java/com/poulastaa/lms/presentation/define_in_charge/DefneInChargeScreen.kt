package com.poulastaa.lms.presentation.define_in_charge

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsClickableTextField
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsListSelector
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun DefneInChargeRootScreen(
    viewModel: DefneInChargeViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is DefneInChargeUiAction.EmitToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    DefneInChargeScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack,
    )
}

@Composable
private fun DefneInChargeScreen(
    state: DefneInChargeUiState,
    onEvent: (DefneInChargeUiEvent) -> Unit,
    navigateBack: () -> Unit,
) {
    val textFieldCColor = TextFieldDefaults.colors(
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
        verticalArrangement = Arrangement.Top,
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
                text = stringResource(id = R.string.define_department_incharge),
                color = MaterialTheme.colorScheme.primaryContainer,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        StoreDetailsListSelector(
            modifier = Modifier.fillMaxWidth(.9f),
            label = stringResource(id = R.string.department),
            text = state.departments.selected,
            isOpen = state.departments.isDialogOpen,
            list = state.departments.all,
            color = textFieldCColor,
            onCancel = {
                onEvent(DefneInChargeUiEvent.OnDepartmentToggle)
            },
            onToggle = {
                onEvent(DefneInChargeUiEvent.OnDepartmentToggle)
            },
            onSelected = {
                onEvent(DefneInChargeUiEvent.OnDepartmentSelect(it))
            }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large2))

        AnimatedVisibility(visible = state.isVisible) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(40.dp)
                                .alpha(if (state.isMakingApiCall) 1f else 0f),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            strokeWidth = 3.dp,
                        )

                        StoreDetailsClickableTextField(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .align(Alignment.Center),
                            text = state.current,
                            label = stringResource(id = R.string.current_head),
                            color = textFieldCColor,
                            isErr = false,
                            errStr = "",
                            onClick = {},
                            disabledContainerColor = MaterialTheme.colorScheme.background.copy(.07f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

                StoreDetailsListSelector(
                    label = stringResource(id = R.string.select_new_teacher),
                    text = state.others.selected,
                    isOpen = state.others.isDialogOpen,
                    list = state.others.teachers.map {
                        it.name
                    },
                    color = textFieldCColor,
                    onCancel = {
                        onEvent(DefneInChargeUiEvent.OnTeacherToggle)
                    },
                    onToggle = {
                        onEvent(DefneInChargeUiEvent.OnTeacherToggle)
                    },
                    onSelected = {
                        onEvent(DefneInChargeUiEvent.OnTeacherSelect(it))
                    }
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.large2))

                Button(
                    modifier = Modifier.fillMaxWidth(.8f),
                    onClick = {
                        onEvent(DefneInChargeUiEvent.OnConformClick)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .alpha(if (state.isDefiningHead) 1f else 0f)
                                .size(40.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                            strokeWidth = 3.dp,
                        )

                        Text(
                            modifier = Modifier
                                .padding(MaterialTheme.dimens.small3 + MaterialTheme.dimens.small2)
                                .align(Alignment.Center)
                                .alpha(if (state.isDefiningHead) 0f else 1f),
                            text = stringResource(id = R.string.continue_text),
                            fontWeight = FontWeight.Medium,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(2f))
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        DefneInChargeScreen(
            state = DefneInChargeUiState(
                isVisible = true
            ),
            onEvent = {}
        ) {

        }
    }
}
package com.poulastaa.lms.presentation.update_balnce

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsListSelector
import com.poulastaa.lms.presentation.store_details.components.StoreDetailsTextFiled
import com.poulastaa.lms.presentation.utils.ScreenWrapper
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun UpdateLeaveBalanceRoot(
    viewModel: UpdateLeaveBalanceViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    UpdateLeaveBalance(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun UpdateLeaveBalance(
    state: UpdateBalanceUiState,
    onEvent: (UpdateBalanceUiEvent) -> Unit,
    navigateBack: () -> Unit,
) {
    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedTextColor = MaterialTheme.colorScheme.background,
        unfocusedTextColor = MaterialTheme.colorScheme.background,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.background,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.background,
        disabledTrailingIconColor = MaterialTheme.colorScheme.background,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
        focusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
        focusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedLabelColor = MaterialTheme.colorScheme.background,
        cursorColor = MaterialTheme.colorScheme.background,
        errorTextColor = MaterialTheme.colorScheme.error,
    )

    ScreenWrapper(
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.dimens.small2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
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
                text = stringResource(id = R.string.update_balance),
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                color = MaterialTheme.colorScheme.background
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

        StoreDetailsListSelector(
            modifier = Modifier.fillMaxWidth(.9f),
            label = stringResource(id = R.string.select_department),
            text = state.department.selected,
            isOpen = state.department.isDialogOpen,
            list = state.department.all,
            color = textFieldColors,
            onCancel = {
                onEvent(UpdateBalanceUiEvent.OnDepartmentToggle)
            },
            onToggle = {
                onEvent(UpdateBalanceUiEvent.OnDepartmentToggle)
            },
            onSelected = {
                onEvent(
                    UpdateBalanceUiEvent.OnDepartmentSelected(it)
                )
            }
        )

        if (state.isRequestingDepartment) {
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

            StoreDetailsListSelector(
                modifier = Modifier.fillMaxWidth(.9f),
                label = stringResource(id = R.string.select_teacher),
                text = state.teacher.selected,
                isOpen = state.teacher.isDialogOpen,
                list = state.teacher.all,
                color = textFieldColors,
                onCancel = {
                    onEvent(UpdateBalanceUiEvent.OnTeacherToggle)
                },
                onToggle = {
                    onEvent(UpdateBalanceUiEvent.OnTeacherToggle)
                },
                onSelected = {
                    onEvent(
                        UpdateBalanceUiEvent.OnTeacherSelected(it)
                    )
                }
            )


            Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

            if (state.isRequestingTeacher) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onBackground
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.dimens.medium1)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(id = R.string.leave_type),
                                modifier = Modifier
                                    .fillMaxWidth(.5f),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.background,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )

                            Text(
                                text = stringResource(id = R.string.leave_balance),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.background,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = MaterialTheme.dimens.medium1)
                        )

                        state.mapOfLeave.forEach {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = MaterialTheme.dimens.large2)
                            ) {
                                Text(
                                    text = it.key,
                                    modifier = Modifier.fillMaxWidth(.78f),
                                    textAlign = TextAlign.Start,
                                    color = MaterialTheme.colorScheme.background,
                                )

                                Text(
                                    text = it.value,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Start,
                                    color = MaterialTheme.colorScheme.background,
                                )
                            }

                            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

                StoreDetailsListSelector(
                    modifier = Modifier.fillMaxWidth(.9f),
                    label = stringResource(id = R.string.select_leave),
                    text = state.listOfLeave.selected,
                    isOpen = state.listOfLeave.isDialogOpen,
                    list = state.listOfLeave.all,
                    color = textFieldColors,
                    onCancel = {
                        onEvent(UpdateBalanceUiEvent.OnLeaveToggle)
                    },
                    onToggle = {
                        onEvent(UpdateBalanceUiEvent.OnLeaveToggle)
                    },
                    onSelected = {
                        onEvent(
                            UpdateBalanceUiEvent.OnLeaveSelected(it)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

                if (state.isRequestingLeave) {
                    Row {
                        Text(
                            text = "${stringResource(id = R.string.leave_balance)}:",
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterVertically),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.background,
                            textDecoration = TextDecoration.Underline,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

                        StoreDetailsTextFiled(
                            modifier = Modifier.fillMaxWidth(),
                            text = state.leaveBalance,
                            label = stringResource(id = R.string.leave_balance),
                            onValueChange = {
                                onEvent(UpdateBalanceUiEvent.OnLeaveBalanceChange(it))
                            },
                            keyboardType = KeyboardType.Number,
                            isErr = false,
                            errText = "",
                            onDone = {
                                onEvent(UpdateBalanceUiEvent.OnContinueClick)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.large2))

                    Button(
                        onClick = { onEvent(UpdateBalanceUiEvent.OnContinueClick) },
                        modifier = Modifier.fillMaxWidth(.7f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        Box {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .alpha(if (state.isMakingApiCall) 1f else 0f),
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 1.dp
                            )

                            Text(
                                text = stringResource(id = R.string.update_balance),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .alpha(if (state.isMakingApiCall) 0f else 1f),
                                letterSpacing = 2.sp,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.large2))
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        UpdateLeaveBalance(
            state = UpdateBalanceUiState(
                mapOfLeave = mapOf(
                    "Chemistry" to "10",
                    "Commerce" to "11",
                    "Computer Science" to "10",
                ),
                isRequestingDepartment = true,
                isRequestingTeacher = true,
                isRequestingLeave = true
            ),
            onEvent = {}
        ) {}
    }
}
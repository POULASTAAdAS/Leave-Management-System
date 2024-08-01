package com.poulastaa.lms.presentation.remove_employee

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.lms.R
import com.poulastaa.lms.presentation.remove_employee.components.TeacherCard
import com.poulastaa.lms.ui.theme.ArrowBackIcon
import com.poulastaa.lms.ui.theme.ArrowDropDownIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens
import com.poulastaa.lms.ui.utils.ObserveAsEvent

@Composable
fun RemoveEmployeeRootScreen(
    viewModel: RemoveEmployeeViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is RemoveEmployeeUiAction.EmitToast -> {
                Toast.makeText(
                    context,
                    it.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    RemoveEmployeeScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateBack = navigateBack,
    )
}

@Composable
private fun RemoveEmployeeScreen(
    state: RemoveEmployeeUiState,
    onEvent: (RemoveEmployeeUiEvent) -> Unit,
    navigateBack: () -> Unit,
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onBackground
                        )
                    )
                )
                .padding(it)
                .padding(horizontal = MaterialTheme.dimens.medium1)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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
                    text = stringResource(id = R.string.remove_employee),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.select_department),
                    color = MaterialTheme.colorScheme.background,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        modifier = Modifier.clickable(
                            onClick = {
                                onEvent(RemoveEmployeeUiEvent.OnDepartmentToggle)
                            },
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ),
                        label = {
                            Text(text = stringResource(id = R.string.department))
                        },
                        value = state.department.selected,
                        onValueChange = {},
                        enabled = false,
                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = Color.Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.background,
                            disabledIndicatorColor = MaterialTheme.colorScheme.background,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.background,
                            disabledLabelColor = MaterialTheme.colorScheme.background
                        ),
                        shape = MaterialTheme.shapes.medium,
                        maxLines = 1,
                        trailingIcon = {
                            Icon(
                                imageVector = ArrowDropDownIcon,
                                contentDescription = null,
                                modifier = Modifier.rotate(
                                    if (state.department.isDialogOpen) 180f else 0f
                                )
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = state.department.isDialogOpen,
                        onDismissRequest = {
                            onEvent(RemoveEmployeeUiEvent.OnDepartmentToggle)
                        },
                        modifier = Modifier
                            .fillMaxWidth(.55f)
                            .heightIn(
                                max = 300.dp
                            )
                            .background(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                    ) {
                        state.department.all.forEachIndexed { index, s ->
                            DropdownMenuItem(
                                modifier = Modifier.background(
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                text = {
                                    Text(
                                        text = s,
                                        color = MaterialTheme.colorScheme.background
                                    )
                                },
                                onClick = {
                                    onEvent(RemoveEmployeeUiEvent.OnDepartmentChange(index))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1)
            ) {
                items(state.teacher) { teacher ->
                    TeacherCard(
                        cookie = state.cookie,
                        teacher = teacher,
                        modifier = Modifier.clickable {

                        }
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
        RemoveEmployeeScreen(state = RemoveEmployeeUiState(
            teacher = (1..5).map {
                UiTeacher(
                    id = it,
                    name = "Teacher: $it",
                    designation = "designation",
                    ""
                )
            },
        ), onEvent = {}) {

        }
    }
}
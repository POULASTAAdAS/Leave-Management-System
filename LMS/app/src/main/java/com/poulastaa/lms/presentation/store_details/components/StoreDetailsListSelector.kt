package com.poulastaa.lms.presentation.store_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poulastaa.lms.R
import com.poulastaa.lms.ui.theme.ArrowDropDownIcon
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun StoreDetailsListSelector(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    isOpen: Boolean,
    list: List<String>,
    color: TextFieldColors,
    onCancel: () -> Unit,
    onToggle: () -> Unit,
    onSelected: (index: Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable(
                onClick = onToggle,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = text,
            onValueChange = {},
            label = {
                Text(text = label)
            },
            shape = MaterialTheme.shapes.medium,
            colors = color.copy(
                disabledTextColor = MaterialTheme.colorScheme.background,
                disabledLabelColor = MaterialTheme.colorScheme.background,
                disabledIndicatorColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = Color.Transparent
            ),
            singleLine = true,
            enabled = false,
            trailingIcon = {
                IconButton(
                    onClick = onToggle
                ) {
                    Icon(
                        modifier = Modifier.rotate(
                            if (isOpen) 180f else 0f
                        ),
                        imageVector = ArrowDropDownIcon,
                        contentDescription = null
                    )
                }
            },
        )

        DropdownMenu(
            modifier = modifier
                .background(MaterialTheme.colorScheme.onBackground)
                .sizeIn(maxHeight = 240.dp),
            expanded = isOpen,
            onDismissRequest = onCancel,
        ) {
            list.forEachIndexed { index, item ->
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.dimens.medium1),
                    text = {
                        Text(
                            text = item,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    },
                    onClick = {
                        onSelected(index)
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.background
                    )
                )

                if (list.lastIndex != index)
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.dimens.medium3)
                            .fillMaxWidth()
                            .height(1.03.dp)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                    )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TestThem {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(MaterialTheme.dimens.medium1),
        ) {
            StoreDetailsListSelector(
                modifier = Modifier
                    .fillMaxWidth(),
                label = stringResource(id = R.string.gender),
                list = listOf("M", "F", "O"),
                text = "",
                isOpen = true,
                onCancel = { /*TODO*/ },
                onToggle = {},
                color = TextFieldDefaults.colors(
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
            ) {

            }
        }
    }
}
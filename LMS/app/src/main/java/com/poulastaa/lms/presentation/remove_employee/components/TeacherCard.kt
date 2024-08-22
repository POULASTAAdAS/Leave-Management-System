package com.poulastaa.lms.presentation.remove_employee.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.poulastaa.lms.presentation.remove_employee.UiTeacher
import com.poulastaa.lms.ui.theme.TestThem
import com.poulastaa.lms.ui.theme.UserIcon
import com.poulastaa.lms.ui.theme.dimens

@Composable
fun TeacherCard(
    modifier: Modifier = Modifier,
    cookie: String,
    teacher: UiTeacher,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.small3),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .addHeader("Cookie", cookie)
                    .data(teacher.profile)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.background.copy(.5f),
                        shape = CircleShape
                    )
                    .background(MaterialTheme.colorScheme.background.copy(.1f)),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = UserIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .padding(MaterialTheme.dimens.small2),
                            tint = MaterialTheme.colorScheme.background.copy(.6f)
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = UserIcon,
                            contentDescription = null
                        )
                    }
                }
            )

            Column {
                Text(
                    text = teacher.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = MaterialTheme.colorScheme.background
                )
                Text(
                    text = teacher.designation,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    color = MaterialTheme.colorScheme.background.copy(.6f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))


            IconButton(
                onClick = onClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(.67f),
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
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
                .background(MaterialTheme.colorScheme.primary)
                .padding(MaterialTheme.dimens.medium1)
        ) {
            TeacherCard(
                cookie = "",
                teacher = UiTeacher(1, "Teacher", "designation", "")
            ){}
        }
    }
}
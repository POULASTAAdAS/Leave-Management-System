package com.poulastaa.lms.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import com.poulastaa.lms.R

val AppLogo: Painter
    @Composable
    get() = painterResource(id = R.drawable.ic_app_logo)

val EmailIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_email)

val CheckIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_check)

val User: ImageVector
    @Composable
    get() = Icons.Rounded.Person

val ArrowBack: ImageVector
    @Composable
    get() = Icons.AutoMirrored.Rounded.ArrowBack
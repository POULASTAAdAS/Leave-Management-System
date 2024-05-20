package com.poulastaa.lms.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun LMSTheme(
    content: @Composable () -> Unit
) {
    val activity = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            val windowsInsetsController =
                WindowCompat.getInsetsController(window, view)

            windowsInsetsController.isAppearanceLightStatusBars = true
            windowsInsetsController.isAppearanceLightNavigationBars = true
        }
    }

    val window = calculateWindowSizeClass(activity = activity as Activity)
    val config = LocalConfiguration.current

    val appDimens: Dimens = when (window.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            if (config.screenWidthDp <= 360) {
                CompactSmallDimens
            } else {
                CompactMediumDimens
            }
        }

        else -> {
            CompactMediumDimens
        }
    }

    AppThem(appDimens = appDimens) {
        MaterialTheme(
            colorScheme = ColorScheme,
            typography = AppTypography,
            shapes = AppShape,
            content = content,
        )
    }
}

@Composable
private fun AppThem(
    appDimens: Dimens,
    content: @Composable () -> Unit
) {
    val dimens = remember {
        appDimens
    }

    CompositionLocalProvider(value = LocalAppDimens provides dimens) {
        content()
    }
}

val LocalAppDimens = compositionLocalOf {
    CompactMediumDimens
}

val MaterialTheme.dimens
    @Composable
    get() = LocalAppDimens.current

@Composable
fun TestThem(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = AppTypography,
        colorScheme = ColorScheme,
        shapes = AppShape,
        content = content
    )
}
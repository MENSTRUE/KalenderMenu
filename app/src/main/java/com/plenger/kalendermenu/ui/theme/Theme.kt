package com.plenger.kalendermenu.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val KalenderMenuColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = NeutralWhite,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealOnContainer,
    secondary = OrangeAI,
    onSecondary = NeutralWhite,
    secondaryContainer = OrangeAILight,
    onSecondaryContainer = NeutralBlack,
    background = NeutralBackground,
    onBackground = NeutralBlack,
    surface = NeutralWhite,
    onSurface = NeutralBlack,
    surfaceVariant = NeutralSurface,
    onSurfaceVariant = NeutralDarkGray,
    outline = NeutralDivider,
    outlineVariant = NeutralLightGray,
    error = StatusRed,
    onError = NeutralWhite,
    errorContainer = StatusRedLight,
    onErrorContainer = StatusRed,
    scrim = Color(0x52000000)
)

@Composable
fun KalenderMenuTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NeutralWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = KalenderMenuColorScheme,
        typography = Typography,
        content = content
    )
}
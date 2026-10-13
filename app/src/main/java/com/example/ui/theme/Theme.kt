package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = StudioPrimaryViolet,
    onPrimary = StudioTextPrimary,
    primaryContainer = StudioPrimaryVioletDark,
    onPrimaryContainer = StudioTextPrimary,
    secondary = StudioCyanAccent,
    onSecondary = StudioDarkBg,
    secondaryContainer = StudioSurfaceElevated,
    onSecondaryContainer = StudioTextPrimary,
    tertiary = StudioNeonPink,
    onTertiary = StudioTextPrimary,
    background = StudioDarkBg,
    onBackground = StudioTextPrimary,
    surface = StudioSurface,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioCard,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioCardBorder,
    outlineVariant = StudioDivider
)

private val LightColorScheme = DarkColorScheme // MotionAI Studio is designed as a cinematic creative suite with dark surface default

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark for video studio experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = StudioDarkBg.toArgb()
                window.navigationBarColor = StudioDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.todoapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = GlassPrimary,
            secondary = GlassSecondary,
            tertiary = GlassTertiary,
            background = GlassBackground,
            surface = GlassSurface,
            surfaceVariant = GlassSurfaceDark,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = GlassTextPrimary,
            onSurface = GlassTextPrimary,
            error = GlassError,
            onError = Color.White
        )
    } else {
        lightColorScheme(
            primary = GlassPrimary,
            secondary = GlassSecondary,
            tertiary = GlassTertiary,
            background = GlassBackgroundLight,
            surface = GlassSurfaceLight,
            surfaceVariant = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = GlassTextPrimaryLight,
            onSurface = GlassTextPrimaryLight,
            error = GlassError,
            onError = Color.White
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

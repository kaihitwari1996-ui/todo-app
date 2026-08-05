package com.example.todoapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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

    // iOS-style typography
    val typography = Typography(
        displayLarge = MaterialTheme.typography.displayLarge.copy(
            fontFamily = FontFamily.Default
        ),
        displayMedium = MaterialTheme.typography.displayMedium.copy(
            fontFamily = FontFamily.Default
        ),
        displaySmall = MaterialTheme.typography.displaySmall.copy(
            fontFamily = FontFamily.Default
        ),
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(
            fontFamily = FontFamily.Default
        ),
        headlineMedium = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Default
        ),
        headlineSmall = MaterialTheme.typography.headlineSmall.copy(
            fontFamily = FontFamily.Default
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontFamily = FontFamily.Default
        ),
        titleMedium = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Default
        ),
        titleSmall = MaterialTheme.typography.titleSmall.copy(
            fontFamily = FontFamily.Default
        ),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Default
        ),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Default
        ),
        bodySmall = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Default
        ),
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontFamily = FontFamily.Default
        ),
        labelMedium = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Default
        ),
        labelSmall = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Default
        )
    )

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
        typography = typography,
        content = content
    )
}

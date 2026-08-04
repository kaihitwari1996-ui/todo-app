package com.example.todoapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val pencilScheme = lightColorScheme(
    primary            = PencilPrimary,
    onPrimary          = Color(0xFFF5F0E8),
    primaryContainer   = PencilAccent,
    onPrimaryContainer = Color(0xFFF5F0E8),
    secondary          = PencilSecondary,
    onSecondary        = Color(0xFFF5F0E8),
    secondaryContainer = PencilSurface,
    onSecondaryContainer = PencilPrimary,
    background         = PencilBackground,
    onBackground       = PencilPrimary,
    surface            = PencilSurface,
    onSurface          = PencilPrimary,
    surfaceVariant     = PencilBackground,
    onSurfaceVariant   = PencilSecondary,
    outline            = Color(0xFFBBB0A0),
    error              = PriorityHigh,
    onError            = Color.White
)

private val classicScheme = lightColorScheme(
    primary            = ClassicPrimary,
    onPrimary          = Color.White,
    primaryContainer   = ClassicSurface,
    onPrimaryContainer = ClassicPrimary,
    secondary          = ClassicSecondary,
    onSecondary        = Color.White,
    secondaryContainer = ClassicSurface,
    onSecondaryContainer = ClassicPrimary,
    background         = ClassicBackground,
    onBackground       = ClassicPrimary,
    surface            = ClassicSurface,
    onSurface          = ClassicPrimary,
    surfaceVariant     = Color(0xFFEEEEEE),
    onSurfaceVariant   = ClassicSecondary,
    outline            = Color(0xFFBBBBBB),
    error              = PriorityHigh,
    onError            = Color.White
)

private val flowScheme = lightColorScheme(
    primary            = FlowPrimary,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFDCEAF8),
    onPrimaryContainer = FlowPrimary,
    secondary          = FlowSecondary,
    onSecondary        = Color.White,
    secondaryContainer = Color(0xFFE8E4FF),
    onSecondaryContainer = FlowSecondary,
    background         = FlowBackground,
    onBackground       = Color(0xFF1A1A1A),
    surface            = FlowSurface,
    onSurface          = Color(0xFF1A1A1A),
    surfaceVariant     = Color(0xFFF0F0F0),
    onSurfaceVariant   = Color(0xFF555555),
    outline            = Color(0xFFDDDDDD),
    error              = PriorityHigh,
    onError            = Color.White
)

private val agroScheme = lightColorScheme(
    primary            = AgroPrimary,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFCEE8C0),
    onPrimaryContainer = AgroPrimary,
    secondary          = AgroSecondary,
    onSecondary        = Color.White,
    secondaryContainer = Color(0xFFEEDFB8),
    onSecondaryContainer = AgroSecondary,
    background         = AgroBackground,
    onBackground       = Color(0xFF1C1C0E),
    surface            = AgroSurface,
    onSurface          = Color(0xFF1C1C0E),
    surfaceVariant     = Color(0xFFE8E4D0),
    onSurfaceVariant   = Color(0xFF5A5A3A),
    outline            = Color(0xFFBBAA80),
    error              = PriorityHigh,
    onError            = Color.White
)

val pencilShapes  = Shapes(small = RoundedCornerShape(2.dp),  medium = RoundedCornerShape(4.dp),  large = RoundedCornerShape(6.dp))
val classicShapes = Shapes(small = RoundedCornerShape(0.dp),  medium = RoundedCornerShape(0.dp),  large = RoundedCornerShape(2.dp))
val flowShapes    = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(16.dp), large = RoundedCornerShape(20.dp))
val agroShapes    = Shapes(small = RoundedCornerShape(4.dp),  medium = RoundedCornerShape(8.dp),  large = RoundedCornerShape(12.dp))

@Composable
fun AppTheme(themeMode: ThemeMode, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = when (themeMode) {
            ThemeMode.PENCIL   -> pencilScheme
            ThemeMode.CLASSIC  -> classicScheme
            ThemeMode.TICKTICK -> flowScheme
            ThemeMode.AGRO     -> agroScheme
        },
        typography = when (themeMode) {
            ThemeMode.PENCIL   -> PencilTypography
            ThemeMode.CLASSIC  -> ClassicTypography
            ThemeMode.TICKTICK -> FlowTypography
            ThemeMode.AGRO     -> AgroTypography
        },
        shapes = when (themeMode) {
            ThemeMode.PENCIL   -> pencilShapes
            ThemeMode.CLASSIC  -> classicShapes
            ThemeMode.TICKTICK -> flowShapes
            ThemeMode.AGRO     -> agroShapes
        },
        content = content
    )
}

package com.example.todoapp.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================
// GLASSMORPHISM + iOS AESTHETIC COLOR PALETTE
// ============================================

// ---- Backgrounds ----
val GlassBackground = Color(0xFF0F1419)        // Deep dark navy
val GlassBackgroundLight = Color(0xFFF2F2F7)   // iOS light gray

// ---- Glass Surfaces ----
val GlassSurface = Color(0x1AFFFFFF)           // 10% white overlay
val GlassSurfaceDark = Color(0x0DFFFFFF)       // 5% white overlay
val GlassSurfaceLight = Color(0xCCFFFFFF)      // 80% white for light mode

// ---- Primary Accents (iOS-style) ----
val GlassPrimary = Color(0xFF00D4FF)           // Cyan
val GlassPrimaryDim = Color(0xFF0099CC)        // Darker cyan
val GlassPrimaryLight = Color(0xFF6FE7FF)      // Light cyan

// ---- Secondary Accents ----
val GlassSecondary = Color(0xFFBF5AF0)         // Purple
val GlassSecondaryLight = Color(0xFFD9B8FF)    // Light purple

// ---- Tertiary Accents ----
val GlassTertiary = Color(0xFFFF375F)          // iOS red/pink
val GlassTertiaryLight = Color(0xFFFF7A92)     // Light red/pink

// ---- Status Colors ----
val GlassSuccess = Color(0xFF34C759)           // iOS green
val GlassSuccessLight = Color(0xFF66E085)      // Light green
val GlassWarning = Color(0xFFFF9500)           // iOS orange
val GlassWarningLight = Color(0xFFFFB143)      // Light orange
val GlassError = Color(0xFFFF3B30)             // iOS red
val GlassErrorLight = Color(0xFFFF6B63)        // Light red

// ---- Grayscale ----
val GlassGray = Color(0xFF8E8E93)              // Medium gray
val GlassGrayLight = Color(0xFFC7C7CC)         // Light gray
val GlassGrayDark = Color(0xFF3A3A3C)          // Dark gray

// ---- Text Colors ----
val GlassTextPrimary = Color(0xFFFFFFFF)       // Pure white (dark mode)
val GlassTextPrimaryLight = Color(0xFF000000)  // Pure black (light mode)
val GlassTextSecondary = Color(0xFFAAAAAA)     // Muted white
val GlassTextTertiary = Color(0xFF808080)      // Faint white

// ---- Glass Overlays ----
val GlassOverlayLight = Color(0x1AFFFFFF)      // Light glass overlay
val GlassOverlayMedium = Color(0x2DFFFFFF)     // Medium glass overlay
val GlassOverlayDark = Color(0x4DFFFFFF)       // Dark glass overlay

// ---- Glass Borders ----
val GlassBorderLight = Color(0x33FFFFFF)       // Subtle white border
val GlassBorderMedium = Color(0x4DFFFFFF)      // Medium white border

// ---- Priority Colors ----
val PriorityHigh = Color(0xFFFF3B30)           // iOS red
val PriorityMedium = Color(0xFFFF9500)         // iOS orange
val PriorityLow = Color(0xFF00D4FF)            // iOS cyan
val PriorityNone = Color(0xFF8E8E93)           // iOS gray

// ---- Habit Colors ----
val HabitColors = listOf(
    Color(0xFF00D4FF),  // Cyan
    Color(0xFF34C759),  // Green
    Color(0xFFBF5AF0),  // Purple
    Color(0xFFFF375F),  // Pink/Red
    Color(0xFFFF9500),  // Orange
    Color(0xFF007AFF)   // iOS Blue
)

// ---- Gradient Definitions ----
val GradientPrimary = listOf(
    Color(0xFF0099FF),  // Blue
    Color(0xFF00D4FF)   // Cyan
)
val GradientSecondary = listOf(
    Color(0xFF9D4EDD),  // Purple
    Color(0xFFBF5AF0)   // Bright purple
)

package com.example.todoapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PencilTypography = Typography(
    headlineLarge  = TextStyle(fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Bold,   fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Bold,   fontSize = 22.sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.Cursive, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.Serif,   fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Serif,   fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.sp),
    labelLarge     = TextStyle(fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Medium, fontSize = 13.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Normal, fontSize = 11.sp)
)

val ClassicTypography = Typography(
    headlineLarge  = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,   fontSize = 28.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,   fontSize = 22.sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 18.sp, letterSpacing = 0.3.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 26.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.sp),
    labelLarge     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.8.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 10.sp, letterSpacing = 1.2.sp)
)

val FlowTypography = Typography(
    headlineLarge  = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold,     fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 22.sp),
    labelLarge     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium,   fontSize = 12.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal,   fontSize = 11.sp)
)

val AgroTypography = Typography(
    headlineLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, letterSpacing = (-1).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,      fontSize = 22.sp, letterSpacing = (-0.5).sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold,  fontSize = 18.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,    fontSize = 16.sp, lineHeight = 25.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,    fontSize = 14.sp, lineHeight = 22.sp),
    labelLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold,  fontSize = 12.sp, letterSpacing = 0.5.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,    fontSize = 11.sp)
)

package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LilacDarkColorScheme = darkColorScheme(
    primary = LilacPrimary,
    onPrimary = Color(0xFF221147),
    primaryContainer = LilacDarkSurfaceVariant,
    onPrimaryContainer = LilacPrimary,
    secondary = LilacSecondary,
    onSecondary = Color(0xFF221147),
    secondaryContainer = LilacDarkSurfaceElevated,
    onSecondaryContainer = LilacSecondary,
    tertiary = LilacTertiary,
    onTertiary = Color(0xFF3E2000),
    background = LilacDarkBackground,
    onBackground = LilacOnDark,
    surface = LilacDarkSurface,
    onSurface = LilacOnDark,
    surfaceVariant = LilacDarkSurfaceElevated,
    onSurfaceVariant = LilacOnDarkVariant,
    outline = LilacDarkBorder,
    outlineVariant = LilacDarkBorderBright
)

private val LilacLightColorScheme = lightColorScheme(
    primary = LilacPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE4FA),
    onPrimaryContainer = Color(0xFF351A6E),
    secondary = Color(0xFF76589B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3EDFA),
    onSecondaryContainer = Color(0xFF2B1842),
    tertiary = Color(0xFFE65100),
    onTertiary = Color.White,
    background = Color(0xFFFAF7FC),
    onBackground = Color(0xFF1E172B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E172B),
    surfaceVariant = Color(0xFFEFE9F6),
    onSurfaceVariant = Color(0xFF4C4260),
    outline = Color(0xFFC8BCD9),
    outlineVariant = Color(0xFFE0D8ED)
)

@Composable
fun SoundShiftTheme(
    darkTheme: Boolean = true, // Default to deep slate lilac dark theme for audio aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LilacDarkColorScheme else LilacLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

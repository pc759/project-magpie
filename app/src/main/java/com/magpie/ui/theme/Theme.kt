package com.magpie.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Vibrant, child-friendly colors
private val PrimaryPurple = Color(0xFF7C3AED)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentGreen = Color(0xFF10B981)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentPink = Color(0xFFEC4899)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    secondary = AccentOrange,
    tertiary = AccentGreen,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    secondary = AccentOrange,
    tertiary = AccentGreen,
    background = Color(0xFFFEF3F2),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1F2937),
    onSurface = Color(0xFF374151)
)

@Composable
fun MagpieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

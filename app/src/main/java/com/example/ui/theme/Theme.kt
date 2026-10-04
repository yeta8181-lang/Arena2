package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color(0xFF1E1400),
    primaryContainer = ArenaSurfaceVariant,
    onPrimaryContainer = GoldAccent,
    secondary = Team1Blue,
    onSecondary = Color.White,
    tertiary = Team2Red,
    onTertiary = Color.White,
    background = ArenaDarkBg,
    onBackground = TextPrimary,
    surface = ArenaSurface,
    onSurface = TextPrimary,
    surfaceVariant = ArenaSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ArenaBorder
)

@Composable
fun HeroArenaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AxiomDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = ObsidianBg,
    secondary = NeonGreen,
    onSecondary = ObsidianBg,
    tertiary = NeonPink,
    onTertiary = TextPrimary,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = HudSurface,
    onSurface = TextPrimary,
    surfaceVariant = HudPanelBg,
    onSurfaceVariant = TextSecondary,
    error = NeonAmber,
    onError = ObsidianBg
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AxiomDarkColorScheme,
        typography = Typography,
        content = content
    )
}

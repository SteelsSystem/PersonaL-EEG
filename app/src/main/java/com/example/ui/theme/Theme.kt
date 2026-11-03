package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AxiomMetallurgiaDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = ObsidianBg,
    secondary = NeonGreen,
    onSecondary = ObsidianBg,
    tertiary = AntiMagCobaltCyan,
    onTertiary = TextPrimary,
    background = SteelAlloyGunmetal,
    onBackground = TextPrimary,
    surface = SteelAlloyBrushedSurface,
    onSurface = TextPrimary,
    surfaceVariant = HudPanelBg,
    onSurfaceVariant = AntiMagAusteniteSilver,
    outline = SteelAlloyChromeEdge,
    error = NeonAmber,
    onError = ObsidianBg
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AxiomMetallurgiaDarkColorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.nammasantheledger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimarySaffron,
    secondary = SecondaryTurmeric,
    tertiary = AccentGreen,
    background = BackgroundCream,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onSecondary = TextDark,
    onTertiary = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark,
    error = ErrorTomato
)

@Composable
fun NammaSantheLedgerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

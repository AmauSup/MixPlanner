package com.supdevinci.mixplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MixOrange,
    onPrimary = MixWhite,
    secondary = MixOrangeDark,
    onSecondary = MixWhite,
    tertiary = MixOrangeLight,
    onTertiary = MixTextDark,
    background = MixOffWhite,
    onBackground = MixTextDark,
    surface = MixWhite,
    onSurface = MixTextDark,
    surfaceVariant = MixCream,
    onSurfaceVariant = MixTextSoft,
    outline = MixBorder,
    error = MixError,
    onError = MixWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = MixOrangeLight,
    onPrimary = MixTextDark,
    secondary = MixOrange,
    onSecondary = MixWhite,
    tertiary = MixOrangeDark,
    onTertiary = MixWhite,
    background = MixTextDark,
    onBackground = MixWhite,
    surface = Color(0xFF2A2A2A),
    onSurface = MixWhite,
    surfaceVariant = Color(0xFF3A2B21),
    onSurfaceVariant = Color(0xFFFFD9BF),
    outline = MixOrangeDark,
    error = MixError,
    onError = MixWhite
)

@Composable
fun MixPlannerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
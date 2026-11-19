package com.jeansock.manager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryGradientStart,
    secondary = TodayGradientEnd,
    background = BackgroundGray,
    surface = CardWhite,
    onPrimary = CardWhite,
    onSecondary = CardWhite,
    onBackground = TextPrimaryBlack,
    onSurface = TextPrimaryBlack
)

@Composable
fun JeanSockManagerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}

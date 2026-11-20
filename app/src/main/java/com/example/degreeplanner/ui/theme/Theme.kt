package com.example.degreeplanner.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RazorbackRed,
    onPrimary = AppleBlossomWhite,
    secondary = SilverLeaf,
    onSecondary = DarkCharcoal,
    tertiary = OldMainRed,
    onTertiary = AppleBlossomWhite,
    background = DarkCharcoal,
    onBackground = SpoofersStoneGray,
    surface = Color(0xFF2E2E2E),
    onSurface = SpoofersStoneGray,
    surfaceVariant = Color(0xFF3A3A3A),
    onSurfaceVariant = SpoofersStoneGray
)

private val LightColorScheme = lightColorScheme(
    primary = RazorbackRed,
    onPrimary = AppleBlossomWhite,
    secondary = OldMainRed,
    onSecondary = AppleBlossomWhite,
    tertiary = SilverLeaf,
    onTertiary = DarkCharcoal,
    background = SpoofersStoneGray,
    onBackground = DarkCharcoal,
    surface = AppleBlossomWhite,
    onSurface = DarkCharcoal,
    surfaceVariant = SilverLeaf,
    onSurfaceVariant = DarkCharcoal
)

@Composable
fun CSPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

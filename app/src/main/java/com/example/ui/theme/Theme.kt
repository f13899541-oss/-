package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = IslamicGreenPrimaryDark,
  onPrimary = Color(0xFF00391B),
  primaryContainer = IslamicGreenContainerDark,
  onPrimaryContainer = Color(0xFFA5F4C7),
  secondary = IslamicGoldDarkTheme,
  onSecondary = Color(0xFF422E00),
  secondaryContainer = Color(0xFF5E4300),
  onSecondaryContainer = Color(0xFFFFE08B),
  tertiary = Color(0xFF81D4AF),
  background = IslamicBackgroundDark,
  onBackground = Color(0xFFE2EBE5),
  surface = IslamicSurfaceDark,
  onSurface = Color(0xFFE2EBE5),
  surfaceVariant = IslamicSurfaceVariantDark,
  onSurfaceVariant = Color(0xFFB5C9BD),
  outline = IslamicBorderDark
)

private val LightColorScheme = lightColorScheme(
  primary = IslamicGreenPrimary,
  onPrimary = Color.White,
  primaryContainer = IslamicGreenContainer,
  onPrimaryContainer = IslamicGreenOnContainer,
  secondary = IslamicGoldDark,
  onSecondary = Color.White,
  secondaryContainer = IslamicGoldLight,
  onSecondaryContainer = Color(0xFF2C1E00),
  tertiary = Color(0xFF166542),
  background = IslamicBackgroundLight,
  onBackground = Color(0xFF141E18),
  surface = IslamicSurfaceLight,
  onSurface = Color(0xFF141E18),
  surfaceVariant = IslamicSurfaceVariantLight,
  onSurfaceVariant = Color(0xFF3F4D44),
  outline = IslamicBorderLight
)

@Composable
fun NoorIslamicTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

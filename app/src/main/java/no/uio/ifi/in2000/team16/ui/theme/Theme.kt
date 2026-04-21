package no.uio.ifi.in2000.team16.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkPrimaryContainer,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkPrimary,
    secondary = DarkSecondary,
    secondaryContainer = DarkPrimary,
    onSecondaryContainer = DarkBackground,
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainerNormal,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    outlineVariant = DarkOutlineVariant,
    error = DangerError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = SurfaceCream,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = SurfaceCream,
    secondaryContainer = PrimaryFixed,
    onSecondaryContainer = OnSurfaceDark,
    background = SurfaceCream,
    surface = SurfaceCream,
    onSurface = OnSurfaceDark,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainerNormal,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    error = DangerError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    surfaceVariant = SurfaceContainerHigh,
    outlineVariant = OutlineVariantLight,
    onSurfaceVariant = OnSurfaceDark
)

@Composable
fun Team16Theme(
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
        content = content,
        shapes = Shapes
    )
}
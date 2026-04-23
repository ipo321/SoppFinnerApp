package no.uio.ifi.in2000.team16.ui.theme

import android.app.Activity
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

    //Primary
    primary = DarkPrimary,
    onPrimary = DarkPrimaryContainer,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkPrimary,

    //Secondary
    secondary = DarkSecondary,
    secondaryContainer = DarkPrimary,
    onSecondaryContainer = DarkBackground,

    // The base layer of the app
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,

    // Tonal Hierarchy: Use these to create depth and stacked layers without using lines
    surfaceContainerLowest = DarkSurfaceContainerLowest, // Deepest layer
    surfaceContainerLow = DarkSurfaceContainerLow, // Main section containers
    surfaceContainer = DarkSurfaceContainerNormal, // Standard component backgrounds
    surfaceContainerHigh = DarkSurfaceContainerHigh, // Elevated cards or nested importance
    surfaceContainerHighest = DarkSurfaceContainerHighest, // Top-level headers or input fields

    // Editorial accents and descriptive hierarchies
    surfaceVariant = DarkSurfaceContainerHigh, // Used for glass effects (apply opacity in UI)
    onSurfaceVariant = DarkOnSurface,

    // Outlines and accessibility
    outlineVariant = DarkOutlineVariant, //"Ghost Borders"

    //toxic/hazardous warnings
    error = DangerError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val LightColorScheme = lightColorScheme(

    //Primary
    primary = PrimaryGreen,
    onPrimary = SurfaceCream,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = SurfaceCream,

    //Secondary
    secondary = PrimaryFixed,
    secondaryContainer = PrimaryFixed,
    onSecondaryContainer = OnSurfaceDark,

    // The base layer of the app
    background = SurfaceCream,
    surface = SurfaceCream,
    onSurface = OnSurfaceDark,

    // Tonal Hierarchy: Use these to create depth and stacked layers without using lines
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainerNormal,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,

    // Editorial accents and descriptive hierarchies
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceDark,

    // Outlines and accessibility
    outlineVariant = OutlineVariantLight, //"Ghost Borders"

    //toxic/hazardous warnings
    error = DangerError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
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
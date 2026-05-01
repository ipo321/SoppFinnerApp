package no.uio.ifi.in2000.team16.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
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
    background = DarkSurface,
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

    //Primary  for important buttons ands actions
    primary = PrimaryGreen,   // Used for buttons, Outlines, Large text/ Headlines
    onPrimary = OnSurfaceDark,   // Things put on top of primary color
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = SurfaceCream,

    //Secondary   Elements not need immediate attention,
    secondary = PrimaryFixed,
    secondaryContainer = PrimaryFixed,
    onSecondaryContainer = OnSurfaceDark,

    // The base layer of the app
    background = SurfaceContainerLowest,    // Use for main background on each screen
    surface = SurfaceCream,       // Used for smaller boxes/surfaces and other that should be the same as background
    onSurface = OnSurfaceDark,   //  Normal text on the background/surface

    // Tonal Hierarchy: Use these to create depth and stacked layers without using lines
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,     //   ^  Use of  boxes inside a box, lower feeling
    surfaceContainer = SurfaceContainerNormal,   //   Use for containers/boxes on the screen on top of background color
    surfaceContainerHigh = SurfaceContainerHigh,   // v Use for boxes inside a box, higher feeling
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

    val padding = Padding()
    CompositionLocalProvider(LocalPadding provides padding) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
            shapes = Shapes
        )
    }
}

object Team16Theme {
    val padding: Padding
        @Composable
        @ReadOnlyComposable
        get() = LocalPadding.current
}
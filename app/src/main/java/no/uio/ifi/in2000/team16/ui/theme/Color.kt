package no.uio.ifi.in2000.team16.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// Light Mode

val PrimaryGreen = Color(0xFF384E1B)
val PrimaryContainer = Color(0xFF4F6630)
val PrimaryFixed = Color(0xFFD0ECA8)   //Statuses and mushrooms

val SurfaceCream = Color(0xFFFFF0D8)  //Background
val OnSurfaceDark = Color(0xFF1B1C15) // Instead of pure black

val DangerError = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerLow = Color(0xFFF6F4E8)
val SurfaceContainerNormal = Color(0xFFF0EEE2)
val SurfaceContainerHigh = Color(0xFFEAE8DD)
val SurfaceContainerHighest = Color(0xFFE5E3D7)

val OutlineVariantLight = Color(0xFFC5C8B9).copy(alpha = 0.15f)

val PrimaryGradientLight = Brush.linearGradient(
    colors = listOf(PrimaryGreen, PrimaryContainer)
)

// Dark Mode
val DarkPrimary = Color(0xFFB5D08E)
val DarkPrimaryContainer = Color(0xFF4F6630)
val DarkSecondary = Color(0xFFACD0AC)
val DarkBackground = Color(0xFF121412)
val DarkSurface = Color(0xFF121412)
val DarkOnSurface = Color(0xFFFCFAED)
val DarkSurfaceContainerLowest = Color(0xFF161816)
val DarkSurfaceContainerLow = Color(0xFF1A1C1A)
val DarkSurfaceContainerNormal = Color(0xFF1E201E)
val DarkSurfaceContainerHigh = Color(0xFF222422)
val DarkSurfaceContainerHighest = Color(0xFF262826)
val DarkOutlineVariant = Color(0xFFC5C8B9).copy(alpha = 0.15f)
val PrimaryGradientDark = Brush.linearGradient(
    colors = listOf(DarkPrimary, DarkPrimaryContainer)
)

// Heatmap/Score colors
val ScoreHigh = Color(0xFF4ADE80) //rgba(74, 222, 128, 0.6f)
val ScoreMediumHigh = Color(0xFFFACC15)  //rgba(250, 204, 21, 0.6f)
val ScoreMediumLow = Color(0xFFFB923C)  //rgba(251, 146, 60, 0.6f)
val ScoreLow = Color(0xFFF87171)  //rgba(248, 113, 113, 0.6f)
//Bottom navigation colors
val DarkGreen = Color(0xFF1E1E1E)
val LightGreen= Color(0xFFD4E6CC)

val LightGreen2 = Color(0xFFEDFEED)



//Bottom navigaiton DarkMode
val BottomBarDark = Color(0xFF1E1E1E)
val selectedColor = Color(0xFFD4E6CC)
val iconColor = Color(0xFFEDFEED)

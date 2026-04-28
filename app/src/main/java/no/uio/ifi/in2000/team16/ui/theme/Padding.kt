package no.uio.ifi.in2000.team16.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Padding(
    val inner: Dp = 8.dp,
    val outer: Dp = 16.dp,
    val outerLarge: Dp = 32.dp,
    val mapButtonsSize: Dp = 56.dp // Not exactly padding, but the size of the buttons on the map screen
    // Add more here when needed
)

val LocalPadding = staticCompositionLocalOf { Padding() }
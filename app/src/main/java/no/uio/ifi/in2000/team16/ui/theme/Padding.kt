package no.uio.ifi.in2000.team16.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Padding(
    val noPadding: Dp = 0.dp,
    val inner: Dp = 8.dp,
    val innerMedium: Dp = 12.dp,
    val outer: Dp = 16.dp,
    val outerMedium: Dp = 24.dp,
    val outerLarge: Dp = 32.dp,
    val mapButtonsSize: Dp = 56.dp, // Not exactly padding, but the size of the buttons on the map screen
    val colorExplanationSize: Dp = 10.dp,
    val searchResultHeight: Dp = 500.dp,
    val borderWidthSmall: Dp = 2.dp,
    val borderWidthMedium: Dp = 4.dp,
    val shadowElevationSmall: Dp = 2.dp,
    val shadowElevationMedium: Dp = 8.dp,
    val spacingMinimum: Dp = 10.dp,
    val spacingSmall: Dp = 16.dp,
    val spacing: Dp = 24.dp,
    val spacingLarge: Dp = 42.dp,
    val scoreCardSpacing: Dp = 20.dp
)

val LocalPadding = staticCompositionLocalOf { Padding() }
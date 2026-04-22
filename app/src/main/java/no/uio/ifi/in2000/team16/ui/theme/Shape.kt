package no.uio.ifi.in2000.team16.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(16.dp),  // Smaller elements, tooltips or small action buttons
    medium = RoundedCornerShape(24.dp),  //Standard components, natural, rounded feel
    large = RoundedCornerShape(48.dp), // Main cards (e.g., Species Cards) and significant UI
    extraLarge = RoundedCornerShape(100)  // Main containers, buttons, chips, and input fields
)
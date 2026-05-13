package no.uio.ifi.in2000.team16.ui.menu

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun Toggle(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    val animateSize by animateDpAsState(
        targetValue = if (checked) 24.dp else 0.dp, // Moves the button when toggled
        label = "toggleAnimation"
    )

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .width(60.dp)
            .height(24.dp)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onCheckedChange() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(animateSize)
        )

        Box(
            modifier = Modifier
                .padding(5.dp)
                .clip(CircleShape)
                .width(24.dp)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}
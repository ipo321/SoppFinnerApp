package no.uio.ifi.in2000.team16.ui.minSide

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
/*
A toggle switch that changes between light mode and dark mode .
 */

@Composable
fun Toggle(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    //Animates the toggle movement
    val animateSize by animateDpAsState(
        targetValue = if (checked) 30.dp else 0.dp,
        label = "toggleAnimation"
    )

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .width(80.dp)
            .height(50.dp)
            .background(
                //The toggle background changes when it is pressed or not
                if (checked) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            )
            .clickable { onCheckedChange() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(animateSize)
        )
/*
The small circle inside the toggle switch
 */
        Box(
            modifier = Modifier
                .padding(5.dp)
                .clip(CircleShape)
                .width(40.dp)
                .height(40.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}
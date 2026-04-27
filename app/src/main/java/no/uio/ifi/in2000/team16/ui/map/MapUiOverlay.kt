package no.uio.ifi.in2000.team16.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme

/*
Smaller elements on hte map like the center button, loading indicators and so on

 */

@Composable
fun ColorRow(
    numberOfRows: Int,
    colors: List<Color>,
    text: List<String>,
    modifier: Modifier = Modifier
) {
    Column() {
        repeat(numberOfRows) { index ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(Team16Theme.padding.inner),
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        start = Team16Theme.padding.outer,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(colors[index])
                )
                Text(
                    text = text[index],
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ColorRowPreview() {
    ColorRow(
        numberOfRows = 3,
        colors = listOf(Color.Red, Color.Blue, Color.Yellow),
        text = listOf("Red", "Blue", "Yellow")
    )
}

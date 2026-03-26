package no.uio.ifi.in2000.team16.ui.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset


@Composable
fun Dropdown() {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.padding(start =16.dp, top = 48.dp)
            .size(50.dp).
        background(Color.White,
                shape = RoundedCornerShape(20.dp))
                .padding(10.dp),
                contentAlignment = Alignment.Center,


    ) {
        IconButton(
            onClick = { expanded = !expanded })
        {
            Icon(Icons.Default.MoreVert,
                contentDescription = "More options",
                modifier = Modifier.size(50.dp))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            // First section
            DropdownMenuItem(
                text = { Text("Min side") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text("Settings") },
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                onClick = { /* Do something... */ }
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text("Kart") },
                leadingIcon = { Icon(Icons.Outlined.Create, contentDescription = null) },
                onClick = { /* Do something... */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DropdownPreview() {
    Dropdown()
}





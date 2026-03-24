package no.uio.ifi.in2000.team16.ui.mushroomInfo

import no.uio.ifi.in2000.team16.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MushroomInfoScreen(viewModel: MushroomInfoViewModel = viewModel()) {
    val selected = viewModel.selectedMushroom
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4E6CC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box {
        Button(onClick = { expanded = true }) {
            Text(selected.commonName)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            viewModel.mushrooms.forEach { mushroom ->
                DropdownMenuItem(
                    text = { Text(mushroom.commonName) },
                    onClick = {
                        viewModel.selectMushroom(mushroom)
                        expanded = false
                    }
                )
            }
        }
    }
        // Mushroom image in a green circle
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color(0xFF8FAF82)),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = selected.imageRes),
                contentDescription = selected.commonName,
                modifier = Modifier.size(160.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = selected.commonName, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = selected.latinName, fontSize = 14.sp, fontStyle = FontStyle.Italic)
    }
}
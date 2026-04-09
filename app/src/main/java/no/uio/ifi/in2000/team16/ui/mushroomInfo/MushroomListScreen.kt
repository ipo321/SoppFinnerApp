package no.uio.ifi.in2000.team16.ui.mushroomInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import no.uio.ifi.in2000.team16.domain.model.Mushroom

@Composable
fun MushroomListScreen(
    onMushroomClick: () -> Unit,
    viewModel: MushroomInfoViewModel = hiltViewModel()
){
    val mushrooms = viewModel.mushrooms

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF426914))
            .padding(24.dp)
    ){
        Text(
            text = "Soppsiden",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF426914))
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            mushrooms.forEach { mushroom ->
                MushroomListItem(
                    mushroom = mushroom,
                    onClick = {
                        viewModel.selectMushroom(mushroom)
                        onMushroomClick()
                    }
                )
                Spacer(Modifier.height(16.dp))
            }
        }

    }
}

@Composable
fun MushroomListItem(
    mushroom: Mushroom,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8DC2EA))
            .padding(12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = mushroom.imageRes),
            contentDescription = mushroom.commonName,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(mushroom.commonName, fontWeight = FontWeight.Bold)
            Text(
                mushroom.latinName,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
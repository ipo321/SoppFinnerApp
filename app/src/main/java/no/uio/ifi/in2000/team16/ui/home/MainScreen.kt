import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team16.ui.home.KartScreen
import no.uio.ifi.in2000.team16.R

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableIntStateOf(1) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        KartScreen()

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Color(0xFFF4F1EE))
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomMenuItem(
                title = "Informasjon",
                iconRes = R.drawable.baseline_info_outline_24,
                selected = selectedItem == 0,
                onClick = { selectedItem = 0 }
            )

            BottomMenuItem(
                title = "Kart",
                iconRes = R.drawable.outline_map_pin_heart_24,
                selected = selectedItem == 1,
                onClick = { selectedItem = 1 },
                highlight = true
            )

            BottomMenuItem(
                title = "Min side",
                iconRes = R.drawable.outline_account_circle_24,
                selected = selectedItem == 2,
                onClick = { selectedItem = 2 }
            )
        }
    }
}

@Composable
fun BottomMenuItem(
    title: String,
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    highlight: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (highlight) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFD7E4D0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(22.dp),
                    tint = Color(0xFF445A44)
                )
            }
        } else {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(22.dp),
                tint = Color(0xFF3F3F3F)
            )
        }

        Text(
            text = title,
            color = Color(0xFF3F3F3F)
        )
    }
}
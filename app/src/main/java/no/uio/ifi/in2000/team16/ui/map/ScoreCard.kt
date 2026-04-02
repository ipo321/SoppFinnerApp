package no.uio.ifi.in2000.team16.ui.map

//Version-3.5

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext


//Initialize it with the arguments from the logic/MushroomScorer data.

@Composable
fun ScoreCard(
    modifier: Modifier = Modifier,
    description: String,
    score: Int,
    contextData: EnvironmentContext?
) {
    val context = LocalContext.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //The Score Cirlce itself:
            Card(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        score > 70 -> Color(0xFF4CAF50)
                        score > 40 -> Color(0xFFFFC107)
                        else -> Color(0xFFF44336)
                    }
                )

            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    //Displays the score evaluation.
                    Text(
                        text = score.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }

            }


            Spacer(Modifier.height(8.dp))

            Text(text = description, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))

            //Rows intended for extra info like humidity exct..:
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    //Uppermost left
                    InfoItem(
                        label = "Skogstype",
                        value = contextData?.forestData?.let { "${it.dominantTreeSpecies}" } ?: "Ukjent"
                    )

                    //Uppermost Right
                    val avgTemp = contextData?.weatherHistory?.avgTempPast14Days
                    InfoItem(
                        label = "Snitt-temp",
                        value = if (avgTemp != null) "%.1f°C".format(avgTemp) else "--"
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val rain = contextData?.weatherHistory?.totalRainPast14Days
                    InfoItem(
                        label = "Nedbør (14 dgr)",
                        value = if (rain != null) "%.1f mm".format(rain) else "--"
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Været nå", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val symbolCode = contextData?.currentWeather?.symbolCode
                            if (symbolCode != null) {
                                val imageRes = context.resources.getIdentifier(symbolCode, "drawable", context.packageName)
                                if (imageRes != 0) {
                                    Image(
                                        painter = painterResource(id = imageRes),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                }
                            }

                            val currentTemp = contextData?.currentWeather?.temp
                            Text(
                                text = if (currentTemp != null) "${currentTemp.toInt()}°C" else "--",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

@Composable
fun InfoItem(label: String, value: String){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}
package no.uio.ifi.in2000.team16.ui.map

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import no.uio.ifi.in2000.team16.R
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.ui.theme.ScoreHigh
import no.uio.ifi.in2000.team16.ui.theme.ScoreLow
import no.uio.ifi.in2000.team16.ui.theme.ScoreMediumHigh
import no.uio.ifi.in2000.team16.ui.theme.ScoreMediumLow
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme

/**
 *  View-component that shows the scorecard in the app.
 *  Initialized with the arguments uiState from ScoreCardViewModel.
*/

@Composable
fun BoxScope.InfoPopUp(
    msg : String,
    score : Int,
    onDismiss: (() -> Unit)?,
    contextData: EnvironmentContext?,
    modifier: Modifier = Modifier
) {
    val uiState = contextData?.toUiState(score, msg) ?: ScoreCardUiState.empty(score, msg)

    ScoreCard(
        uiState = uiState,
        onDismiss = onDismiss,
        modifier = modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(
                top = Team16Theme.padding.outer,
                end = Team16Theme.padding.outer,
                start = Team16Theme.padding.outer,
                bottom = Team16Theme.padding.outer
            )
    )
}

@SuppressLint("LocalContextResourcesRead", "DiscouragedApi")
@Composable
fun ScoreCard(
    uiState: ScoreCardUiState,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {
    LocalContext.current

    //controls whether the card is extended or not
    var isExpanded by remember {mutableStateOf(false)}

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = Team16Theme.padding.shadowElevationMedium)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {


            //Collapsed view:
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(Team16Theme.padding.outer)
                    .padding(top = Team16Theme.padding.inner),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(Team16Theme.padding.spacing))

                //The Score Cirlce itself:
                Box(contentAlignment = Alignment.Center) {
                    ScoreCircle(score = uiState.score)
                    InfoCapsule(
                        infoText = "Scoren viser hvor godt egnet punktet pin-en er satt på er for " +
                                " å finne sopp på en skala fra 0-100. Den baserer seg på værhistorikk og " +
                                "skogstype.",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 30.dp, y = (-20).dp)
                    )
                }

                Spacer(Modifier.height(Team16Theme.padding.spacingMinimum))

                Text(
                    text = uiState.description,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(Team16Theme.padding.scoreCardSpacing))

                DisplayInfoItem(
                    "Snitt-temp (14d)", uiState.avgTemp,
                    "Nedbør (14d)", uiState.totalRain
                )

                Spacer(Modifier.height(Team16Theme.padding.scoreCardSpacing))

                WeatherRow(uiState)

                //Extended view
                AnimatedVisibility(visible = isExpanded) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Team16Theme.padding.scoreCardSpacing),
                    ) {
                        DisplayInfoItem(
                            "Temp", uiState.currentTemp,
                            "Regn", uiState.rain
                        )
                        DisplayInfoItem(
                            "Fukt", uiState.humidity,
                            "Vind", uiState.windSpeed
                        )
                        DisplayInfoItem(
                            "Minste temp", uiState.minTemp,
                            "Max temp", uiState.maxTemp
                        )
                        DisplayInfoItem(
                            "Gran", uiState.sprucePercent,
                            "Furu", uiState.pinePercent
                        )
                        DisplayInfoItem(
                            "Lauv", uiState.broadleafPercent,
                            "Bonitet", uiState.fertility
                        )
                    }
                }
            }

            //Always visible in same spaces:

            // Close button up in the corner
            if (onDismiss != null) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .zIndex(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Lukk",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .size(32.dp)
                    .zIndex(1f)
            ) {
                Icon(
                    modifier = Modifier.scale(scaleX = -1f, scaleY = 1f), // mirrors the icon
                    painter = painterResource(
                        id = if (isExpanded) R.drawable.close_fullscreen_24px
                        else R.drawable.open_in_full_24px
                    ),
                    contentDescription = if (isExpanded) "Krymp" else "Utvid",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
    }
}


fun getInterpolatedScoreColor(score: Int): Color {
    val s = score.coerceIn(0, 100).toFloat()
    return when {
        s <= 15f -> lerp(ScoreLow, ScoreMediumLow, s / 15f)
        s <= 50f -> lerp(ScoreMediumLow, ScoreMediumHigh, (s - 15f) / 35f)
        else -> lerp(ScoreMediumHigh, ScoreHigh, (s - 50f) / 50f)
    }
}


//Makes the scoreCirlce and the ring around it:
@Composable
fun ScoreCircle(score: Int, modifier: Modifier = Modifier){
    val scoreColor = getInterpolatedScoreColor(score)
    val trackColor = scoreColor.copy(alpha = 0.25f)
    val sweepAngle = (score /100f) * 360f


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(100.dp)
    ){
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val inset = strokeWidth / 2

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(
                    width = size.width - strokeWidth,
                    height = size.height - strokeWidth
                ),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = scoreColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(
                    width = size.width - strokeWidth,
                    height = size.height - strokeWidth
                ),
                style = Stroke(width = inset + 1.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        //The Score Cirlce itself:
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = getInterpolatedScoreColor(score)
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
                    color = MaterialTheme.colorScheme.background
                )
            }

        }

        
    }

}

@Composable
fun InfoCapsule(
    infoText: String,
    modifier: Modifier = Modifier
){
    var isExpanded by remember {mutableStateOf(false)}

    //So you can click anywhere on the screen to exit the infopopup
    Box(modifier = modifier) {

        //The infocapsule
        Surface(
            onClick = { isExpanded = !isExpanded },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_info_outline_24),
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(6.dp)
                    .size(18.dp)
            )
        }

        if(isExpanded){
            Popup(
                onDismissRequest = {isExpanded = false},
                properties = PopupProperties(focusable = true)
            ){
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .widthIn(max = 240.dp)
                        .padding(8.dp)
                ){
                    Text(
                        text = infoText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayInfoItem(
    label1: String, value1: String,
    label2: String, value2: String,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier.fillMaxWidth()){
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center){InfoItem(label1, value1)}
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center){InfoItem(label2, value2)}
    }
}

@Composable
private fun WeatherRow(uiState: ScoreCardUiState){
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {

            InfoItem(
                label = "Dominant tre-art",
                value = uiState.dominantTreeSpecies
            )
        }

        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Været nå",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    if (uiState.symbolCode != null) {
                        val imageRes = context.resources.getIdentifier(
                            uiState.symbolCode,
                            "drawable",
                            context.packageName
                        )
                        if (imageRes != 0) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                    }


                    Text(
                        text = uiState.currentTemp,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
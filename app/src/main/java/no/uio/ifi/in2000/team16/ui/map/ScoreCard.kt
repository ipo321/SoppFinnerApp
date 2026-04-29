package no.uio.ifi.in2000.team16.ui.map

//Version-3.5

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import no.uio.ifi.in2000.team16.ui.theme.*
import androidx.compose.ui.graphics.lerp
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import no.uio.ifi.in2000.team16.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import no.uio.ifi.in2000.team16.ui.dev.GridRow
import no.uio.ifi.in2000.team16.ui.dev.SectionHeader

//Initialize it with the arguments from the logic/MushroomScorer data.

@Composable
fun BoxScope.InfoPopUp(
    msg : String,
    score : Int,
    onDismiss: (() -> Unit)?,
    contextData: EnvironmentContext?,
    modifier: Modifier = Modifier
) {
    ScoreCard(
        score = score,
        description = msg,
        contextData = contextData,
        onDismiss = onDismiss,
        modifier = Modifier
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

@Composable
fun ScoreCard(
    modifier: Modifier = Modifier,
    description: String,
    score: Int,
    onDismiss: (() -> Unit)? = null,
    contextData: EnvironmentContext?
) {
    val context = LocalContext.current
    //controls weather the card is extended or not
    var isExpanded by remember {mutableStateOf(false)}

    val w = contextData?.currentWeather
    val h = contextData?.weatherHistory
    val f = contextData?.forestData
    val t = contextData?.terrainData


    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()){

            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = {
                    (fadeIn() + slideInVertically{it}) togetherWith
                    (fadeOut() + slideOutVertically {it})
                },
                label = "scorecard_expand"
            ){
                expanded ->
                if(expanded){
                    //Expanded view:

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(24.dp))

                        //The Score Cirlce itself:
                        Box(contentAlignment = Alignment.Center) {
                            ScoreCircle(score = score)
                            InfoCapsule(
                                infoText = "Scoren viser hvor godt egnet punktet pin-en er satt på er for " +
                                        " å finne sopp på en skala fra 0-100. Den baserer seg på værhistorikk og " +
                                        "skogstype.",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 30.dp, y = (-20).dp)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = description,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(16.dp))

                        //Rows intended for extra info like humidity exct :
                        Row(modifier = Modifier.fillMaxWidth()){
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Snitt-temp",
                                    h?.avgTempPast14Days.let {"%.1f°C".format(it)} ?: "--")
                            }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Nedbør (14d)",
                                    h?.totalRainPast14Days?.let { "%.1f mm".format(it) } ?: "--")
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val rain = contextData?.weatherHistory?.totalRainPast14Days
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {

                                InfoItem(
                                    label = "Nedbør (14 dgr)",
                                    value = if (rain != null) "%.1f mm".format(rain) else "--"
                                )
                            }

                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                    Text(
                                        text = "Været nå",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center) {
                                        val symbolCode = contextData?.currentWeather?.symbolCode
                                        if (symbolCode != null) {
                                            val imageRes = context.resources.getIdentifier(
                                                symbolCode,
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


                                        val currentTemp = contextData?.currentWeather?.temp
                                        Text(
                                            text = if (currentTemp != null) "${currentTemp.toInt()}°C" else "--",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }


                        Spacer(Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth()){
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Temp", "${w?.temp?.toInt() ?: "--"}°C")
                            }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Regn", "${w?.rain ?: "--"} mm")
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth()){
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Fukt",
                                    w?.humidity?.let {"%.2f%%".format(it)} ?: "--")
                            }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Vind", "${w?.windSpeed ?: "--"} m/s")
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth()){
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Minste temp",
                                    h?.minTempPast14Days?.let { "%.1f°C".format(it) } ?: "--")

                            }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Max temp",
                                    h?.maxTempPast14Days?.let { "%.1f°C".format(it) } ?: "--")

                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth()){
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Gran", "${f?.sprucePercentage ?: "--"}%")
                            }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Furu", "${f?.pinePercentage ?: "--"}")
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth()){
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Lauv", "${f?.broadleafPercentage ?: "--"}%")
                            }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Bonitet", f?.fertilityCategory ?: "--")
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }

                }else{
                    //Collapsed view:
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(24.dp))

                        //The Score Cirlce itself:
                        Box(contentAlignment = Alignment.Center) {
                            ScoreCircle(score = score)
                            InfoCapsule(
                                infoText = "Scoren viser hvor godt egnet punktet pin-en er satt på er for " +
                                        " å finne sopp på en skala fra 0-100. Den baserer seg på værhistorikk og " +
                                        "skogstype.",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 30.dp, y = (-20).dp)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = description,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth()){
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Snitt-temp",
                                    h?.avgTempPast14Days.let {"%.1f°C".format(it)} ?: "--")
                            }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center){
                                InfoItem("Nedbør (14d)",
                                    h?.totalRainPast14Days?.let { "%.1f mm".format(it) } ?: "--")
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val rain = contextData?.weatherHistory?.totalRainPast14Days
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {

                                InfoItem(
                                    label = "Nedbør (14 dgr)",
                                    value = if (rain != null) "%.1f mm".format(rain) else "--"
                                )
                            }

                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                    Text(
                                        text = "Været nå",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        val symbolCode = contextData?.currentWeather?.symbolCode
                                        if (symbolCode != null) {
                                            val imageRes = context.resources.getIdentifier(
                                                symbolCode,
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


                                        val currentTemp = contextData?.currentWeather?.temp
                                        Text(
                                            text = if (currentTemp != null) "${currentTemp.toInt()}°C" else "--",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Always visibles in same spaces:

            // Close button up in the corner
            if(onDismiss != null){
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                ){
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Lukk",
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }

            IconButton(
                onClick = {isExpanded = !isExpanded},
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .size(32.dp)
            ){
                Icon(
                    painter = painterResource(
                        id = if(isExpanded)R.drawable.close_fullscreen_24px
                        else R.drawable.open_in_full_24px
                    ),
                    contentDescription = if(isExpanded) "Krymp" else "Utvid",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


        }
    }
}

@Composable
fun InfoItem(label: String, value: String){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
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
                    color = MaterialTheme.colorScheme.surface
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
            color = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_info_outline_24),
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
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


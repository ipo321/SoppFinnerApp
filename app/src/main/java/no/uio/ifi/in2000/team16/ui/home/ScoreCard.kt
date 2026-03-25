package no.uio.ifi.in2000.team16.ui.home

//Version-3.0

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorer
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorerV2


//Initialize it with the arguments from the logic/MushroomScorer data.

@Composable
fun ScoreCard(modifier: Modifier = Modifier,
              title: String,
              score: Int,
              description: String ){

    Card(
        modifier = modifier,
        shape = RectangleShape

    ){
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //The ScoreCirlce itself:
            Card(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(Color.Red),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)

            ) {
                Box(
                    modifier = Modifier
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    //Displays the score evaluation.
                    Text(score.toString())
                }

                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center

                ){
                    Text(description)
                }
            }


            Spacer(Modifier.height(8.dp))

            //Rows intended for extra info like humidity exct..:

            Row {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center

                    ){
                        Text(title)
                    }

                    Spacer(Modifier.height(4.dp))

                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ){
                        Text("'Example3'")
                    }
                }

                Spacer(Modifier.width(8.dp))

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ){
                        Text("Example2")
                    }

                    Spacer(Modifier.height(4.dp))

                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ){
                        Text("Example4")
                    }
                }

            }
        }

    }
}
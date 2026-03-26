package no.uio.ifi.in2000.team16.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/*
Per nå bare hjelpemetoder for bottom navigation
 */


sealed class Screen(val rout: String) {

    object MinSide: Screen("Min_side")
    object Score: Screen("Score")
    object Soppsiden: Screen("Soppsiden")
}
@Composable
fun Soppsiden(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Soppsiden",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
fun MinSide(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Profile screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
@Composable
fun Score(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Score",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
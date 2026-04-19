package no.uio.ifi.in2000.team16.ui.navigation

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


sealed class Screen(val route: String) {

    object ProfileScreen: Screen("ProfileScreen")
    object Soppsiden: Screen("Soppsiden")
    object Map : Screen("Kart")
    object OnboardScreen : Screen("OnboardScreen")
    object MushroomInfo: Screen("mushroomInfo")



}

/*
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
*/
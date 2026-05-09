package no.uio.ifi.in2000.team16.ui.navigation

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
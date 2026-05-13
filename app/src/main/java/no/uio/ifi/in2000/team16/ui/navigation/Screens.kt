package no.uio.ifi.in2000.team16.ui.navigation

/*
Helper methods
 */


sealed class Screen(val route: String) {


    object Map : Screen("Kart")
    object OnboardScreen : Screen("OnboardScreen")




}
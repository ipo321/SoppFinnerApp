package no.uio.ifi.in2000.team16.ui.navigation

/*
Per nå bare hjelpemetoder for bottom navigation
 */


sealed class Screen(val route: String) {


    object Map : Screen("Kart")
    object OnboardScreen : Screen("OnboardScreen")




}
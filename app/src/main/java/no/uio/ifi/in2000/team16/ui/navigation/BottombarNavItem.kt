package no.uio.ifi.in2000.team16.ui.navigation

import androidx.compose.ui.graphics.painter.Painter

/*
BottomNavigation items class to keep it clean
 */
data class BottombarNavItem(
    val title: String,
    val icon: Painter,
    val route: String
)
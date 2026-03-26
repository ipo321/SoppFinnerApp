package no.uio.ifi.in2000.team16.ui.home

import androidx.compose.ui.graphics.vector.ImageVector

/*
BottomNavigation items class to keep it clean
 */
data class BottombarNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)
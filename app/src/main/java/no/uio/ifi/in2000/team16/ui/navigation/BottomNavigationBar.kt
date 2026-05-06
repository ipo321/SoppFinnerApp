package no.uio.ifi.in2000.team16.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team16.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues

import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.res.stringResource

private val NavBarContentHeight = 64.dp //for the bottomBarSize
@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navigationItems = listOf(

        BottombarNavItem(
            title = stringResource(R.string.mushroom_list_title),
            icon = painterResource(id= R.drawable.black_mushroom),
            route = MUSHROOM_LIST_SCREEN
        ),
        BottombarNavItem(
            title = stringResource(R.string.kart_big_k),
            icon = rememberVectorPainter(Icons.Outlined.Map),
            route = MAP_SCREEN
        ),


        BottombarNavItem(
            title = stringResource(R.string.meny),
            icon = rememberVectorPainter(Icons.Default.Menu),
            route = MENU_SCREEN
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val activeRoute = parentTabRoute(currentRoute)
    val gestureInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()// Prevent bottom bar from overlapping system navigation

    NavigationBar(
       modifier = Modifier.height(NavBarContentHeight + gestureInset),
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = activeRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true }
                },
                icon = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.surface,

                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()

    BottomNavigationBar(
        navController = navController
    )
}
private fun parentTabRoute(route: String?): String? = when (route) {
    MUSHROOM_LIST_SCREEN, MUSHROOM_INFO_SCREEN -> MUSHROOM_LIST_SCREEN
    MAP_SCREEN                                 -> MAP_SCREEN
    MENU_SCREEN,
    SETTINGS_SCREEN, HISTORY_SCREEN,
    SHROOMSPOT_SCREEN, INFO_SCREEN,
    POISON_CONTROL, SCORE_INFO_SCREEN,
    SECURITY_SCREEN -> MENU_SCREEN
    SHROOMCONTROL_SCREEN                       -> MUSHROOM_LIST_SCREEN
    else                                       -> route
}
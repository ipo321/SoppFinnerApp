package no.uio.ifi.in2000.team16.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navigationItems = listOf(
        BottombarNavItem(
            title = "Kart",
            icon = Icons.Default.Place,
            route = MAP_SCREEN
        ),
        
        BottombarNavItem(
            title = "Soppesiden",
            icon = Icons.Default.Favorite,
            route = MUSHROOM_INFO_SCREEN
        ),

        BottombarNavItem(
            title = "Min side",
            icon = Icons.Default.Home,
            route = Screen.MinSide.rout
        )
    )

    var selectedNavigationIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar(
        containerColor = Color.White
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex == index,
                onClick = {
                    selectedNavigationIndex = index
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true }
                          },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (index == selectedNavigationIndex) {
                            Color.Black
                        } else {
                            Color.Gray
                        }
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFDE8E8),
                    indicatorColor = Color(0xFFD4E6CC)
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
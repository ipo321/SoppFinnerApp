package no.uio.ifi.in2000.team16.ui.navigation

import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team16.R
@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navigationItems = listOf(

        BottombarNavItem(
            title = "Soppesiden",
            icon = painterResource(id= R.drawable.black_mushroom),
            route = MUSHROOM_INFO_SCREEN
        ),
        BottombarNavItem(
            title = "Kart",
            icon = rememberVectorPainter(Icons.Default.Place),
            route = MAP_SCREEN
        ),


        BottombarNavItem(
            title = "Min side",
            icon = rememberVectorPainter(Icons.Default.Home),
            route = Screen.ProfileScreen.route
        )
    )

    var selectedNavigationIndex by rememberSaveable {
        mutableIntStateOf(1)
    }

    NavigationBar(
        containerColor = Color(0xFFEDFEED)
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
                        painter = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
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
                    selectedIconColor = Color(0xFF1E1E1E),
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


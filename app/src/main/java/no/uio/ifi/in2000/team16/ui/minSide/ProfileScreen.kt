package no.uio.ifi.in2000.team16.ui.minSide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


    @Composable
    fun ProfileScreen(
        navController: NavController,
        viewModel: ProfileScreenViewModel = hiltViewModel()

    ){
        //Test to so if its loading in the terminal:
        println(" ProfileScreen loaded")
        var expanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD4E6CC))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Button(onClick = { expanded = true }) {
                    Text("DropDown Menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {

                    //Add a new menuitem for new underscreens

                    DropdownMenuItem(
                        text = {Text("Settings")},
                        onClick = {
                            navController.navigate("profileScreen/settings")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("History")},
                        onClick = {
                            navController.navigate("profileScreen/history")
                            expanded = false
                        }
                    )


                    //Options is a placeholder for the repository holding all
                    //lesser page-menu-items like "settings
     /*
                    viewModel.options.forEach { mushroom ->
                        DropdownMenuItem(
                            text = { Text(mushroom.optionName) },
                            onClick = {
                                viewModel.selectedOption(option)
                                expanded = false
                            }
                        )
                    }
              */ }
            }
        }
    }


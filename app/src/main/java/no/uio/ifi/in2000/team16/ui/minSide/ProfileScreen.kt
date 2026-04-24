package no.uio.ifi.in2000.team16.ui.minSide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.ui.navigation.SHROOMCONTROL_SCREEN


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
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Button(
                    onClick = { expanded = true },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Meny",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
                ) {

                    //Add a new menuitem for new underscreens

                    DropdownMenuItem(
                        text = {Text("Innstillinger")},
                        onClick = {
                            navController.navigate("profileScreen/settings")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Historikk")},
                        onClick = {
                            navController.navigate("profileScreen/history")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Mine soppsteder")},
                        onClick = {
                            navController.navigate(SHROOMCONTROL_SCREEN)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Ansvarserklæring")},
                        onClick = {
                            navController.navigate("profileScreen/info")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Digital Soppkontroll")},
                        onClick = {
                            navController.navigate("profileScreen/shroomControl")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Mushroom-bank")},
                        onClick = {
                            navController.navigate("profileScreen/shroomBank")
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


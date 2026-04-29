package no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions




import android.content.Intent
import android.net.Uri
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreenViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController



@Composable
fun PoisonControl(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: ProfileScreenViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
){
    val context = LocalContext.current

    //Test to so if its loading in the terminal:
    println("PoisonScreen loaded")


    val poisonInformationNmbr = "22591300"
    val intentPIN = Intent(Intent.ACTION_DIAL).apply{
        data = Uri.parse("tel:$poisonInformationNmbr")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4E6CC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text("Tilbake")
            }
        Spacer(modifier=Modifier.height(10.dp))

        Text(
            text = "Giftsentralen",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier=Modifier.height(34.dp))

        Button(
            onClick = {
                context.startActivity(intentPIN)
            }

        ) {
            Text("Ring")
        }




    }
}


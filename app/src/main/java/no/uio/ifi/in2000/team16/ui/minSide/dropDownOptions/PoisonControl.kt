package no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions




import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreenViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItemDefaults.contentColor
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
import kotlinx.serialization.json.JsonNull.content
import org.w3c.dom.Text


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
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.wrapContentSize(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
        ) {
            Text("Tilbake")
        }
        Spacer(modifier=Modifier.height(10.dp))

        Text(
            text = "Giftinformasjonen",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier=Modifier.height(34.dp))

        OutlinedButton(
            onClick = { context.startActivity(intentPIN) },
            modifier = Modifier.wrapContentSize(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
        ) {
            Text("Ring")
        }

    }
}


package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions




import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import no.uio.ifi.in2000.team16.ui.menu.MenuViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.ui.common.BackButton


@Composable
fun PoisonControl(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: MenuViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
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
        BackButton(Alignment.CenterStart, onNavigateBack)

        Spacer(modifier=Modifier.height(10.dp))

        Text(
            text = "Giftinformasjonen",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )

        Spacer(modifier=Modifier.height(34.dp))

        OutlinedButton(
            onClick = { context.startActivity(intentPIN) },
            modifier = Modifier.wrapContentSize(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
        ) {
            Text("Ring")
        }

    }
}


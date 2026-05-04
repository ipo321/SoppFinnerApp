package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import no.uio.ifi.in2000.team16.ui.common.BackButton
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme

@Composable
fun FromMenuDefaultScreen(
    header: String,
    body: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    bodyCentered: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Team16Theme.padding.outerMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(Alignment.CenterStart, onNavigateBack)
        Spacer(modifier=Modifier.height(Team16Theme.padding.outerMedium))
        Text(
            text = header,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier=Modifier.height(Team16Theme.padding.outerMedium))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )

        if (bodyCentered != null) {
            Spacer(modifier=Modifier.height(Team16Theme.padding.outerMedium))
            Text(
                text = bodyCentered,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // The page for poison control needs a call button
        val context = LocalContext.current
        val poisonInformationNmbr = "22591300"
        val intentPIN = Intent(Intent.ACTION_DIAL).apply{
            data = "tel:$poisonInformationNmbr".toUri()
        }

        if (header == "Giftinformasjonen") {
            Spacer(modifier=Modifier.height(Team16Theme.padding.outerMedium))
            OutlinedButton(
                onClick = { context.startActivity(intentPIN) },
                modifier = Modifier.wrapContentSize(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
            ) {
                Text("Ring Giftinformasjonen")
            }
        }
    }
}
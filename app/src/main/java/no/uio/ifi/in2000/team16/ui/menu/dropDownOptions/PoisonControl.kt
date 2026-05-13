package no.uio.ifi.in2000.team16.ui.menu.dropDownOptions

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme


@Composable
fun PoisonControl(
    onNavigateBack: () -> Unit,
){
    FromMenuDefaultScreen(
        header = "Giftinformasjonen",
        body = "Dersom du mistenker soppforgiftning bør du ta kontakt med Giftinformasjonen umiddelbart. De kan nås hele døgnet.",
        onNavigateBack = onNavigateBack,
        content = { PoisonControlContent() }
    )
}

@Composable
fun ColumnScope.PoisonControlContent() {
    Spacer(modifier = Modifier.height(Team16Theme.padding.outerMedium))
    Text(
        text = "TLF: 22 59 13 00",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    // The page for poison control needs a call button
    val context = LocalContext.current
    val poisonInformationNmbr = "22591300"
    val intentPIN = Intent(Intent.ACTION_DIAL).apply{
        data = "tel:$poisonInformationNmbr".toUri()
    }

    Spacer(modifier = Modifier.height(Team16Theme.padding.outerMedium))
    OutlinedButton(
        onClick = { context.startActivity(intentPIN) },
        modifier = Modifier.wrapContentSize(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Text(text = "Ring Giftinformasjonen", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}


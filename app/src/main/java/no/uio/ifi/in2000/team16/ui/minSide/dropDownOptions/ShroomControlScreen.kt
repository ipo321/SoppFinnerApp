package no.uio.ifi.in2000.team16.ui.minSide.dropDownOptions

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team16.ui.common.BackButton
import no.uio.ifi.in2000.team16.ui.minSide.ProfileScreenViewModel
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme


//Overlays the screen with a networkScreen of SoppPortal
@Composable
fun ShroomControlScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: ProfileScreenViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    Box {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadUrl("https://app.soppkontroll.no/")
                }
            }
        )
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(Team16Theme.padding.outer),
            contentAlignment = Alignment.BottomStart
        ) {
            BackButton(
                alignment = Alignment.BottomStart,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}


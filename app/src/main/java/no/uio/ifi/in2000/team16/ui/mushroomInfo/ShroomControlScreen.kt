package no.uio.ifi.in2000.team16.ui.mushroomInfo

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import no.uio.ifi.in2000.team16.ui.common.BackButton
import no.uio.ifi.in2000.team16.ui.theme.Team16Theme


//Overlays the screen with a networkScreen of SoppPortal
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ShroomControlScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box {

        AndroidView(
            modifier = modifier.fillMaxSize(),
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
                textColor = MaterialTheme.colorScheme.secondary,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
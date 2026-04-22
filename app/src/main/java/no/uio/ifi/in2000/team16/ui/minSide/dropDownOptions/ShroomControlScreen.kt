package no.uio.ifi.in2000.team16.ui.minSide

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun ShroomControlScreen(
    navController: NavController,
    viewModel: ProfileScreenViewModel = hiltViewModel()
){

    AndroidView(factory = {context ->
        WebView(context).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl("https://app.soppkontroll.no/")
        }
    })
}



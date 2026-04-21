package no.uio.ifi.in2000.team16.ui.minSide

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView


//Overlays the screen with a networkScreen of SoppPortal
@Composable
fun ShroomControlScreen(){

    AndroidView(factory = {context ->
        WebView(context).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl("https://app.soppkontroll.no/")
        }
    })
}



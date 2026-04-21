package no.uio.ifi.in2000.team16.ui.minSide

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

/*
@Composable
fun ShroomControlScreen(
    navController: NavController,
    viewModel: ProfileScreenViewModel = hiltViewModel()

){
    //Test to so if its loading in the terminal:
    println("ShroomControlScreen loaded")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4E6CC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box{
            Text("This is the ShroomControlScreen")
        }

        Box {
            Button(
                onClick = {
                    navController
                }
            ) {
                Text("Go Back")
            }
        }

        Box {
            Button(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("Go Back")
            }
        }
    }
}
*/

@Composable
fun ShroomControlScreen(){

    AndroidView(factory = {context ->
        WebView(context).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl("https://soppkontroll.no/")
        }
    })
}



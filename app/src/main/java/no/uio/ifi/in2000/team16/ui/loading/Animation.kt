package no.uio.ifi.in2000.team16.ui.loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.team16.R

/*
Loading screen animation
 Multiple images are switched quickly to create
 a simple animated loading effect.
 */
@Composable
fun ImageAnimation(
    modifier: Modifier = Modifier,
    images: List<Int>,
    frameDuration: Long = 200L
) {
//Keeps trach of which image frame is shown, starts with zero
    var currentFrame by remember { mutableIntStateOf(0) }

    // Repeats "forever" and changes image after a delay
        LaunchedEffect(images) {
        while (true) {
            delay(frameDuration)
            //loops through the images
            currentFrame = (currentFrame + 1) % images.size
        }
    }
    // Displays the current animation frame
    Image(
        painter = painterResource(id = images[currentFrame]),
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
fun LoadingScreen() {
    //List of images used for the animation.
    val animationImages = listOf(
        R.drawable.sprite02,
        R.drawable.sprite03,
        R.drawable.sprite04,
        R.drawable.sprite01
    )

    MaterialTheme {
        Column(
            //centers the animation on the screen
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageAnimation(
                //starts the animation image sequence
                images = animationImages,
                modifier = Modifier.size(200.dp),
                frameDuration = 150L
            )
        }
    }
}

@Composable
@Preview
fun LoadingScreenPreview() {
    LoadingScreen()
}

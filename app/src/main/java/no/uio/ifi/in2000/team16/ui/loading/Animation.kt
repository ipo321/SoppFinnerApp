//package no.uio.ifi.in2000.team16.ui.loading
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import com.stevdza_san.sprite.component.SpriteView
//import com.stevdza_san.sprite.domain.SpriteSheet
//import com.stevdza_san.sprite.domain.SpriteSpec
//import com.stevdza_san.sprite.domain.rememberSpriteState
//import com.stevdza_san.sprite.util.getScreenHeight
//import com.stevdza_san.sprite.util.getScreenWidth
//
//@Composable
//@Preview
//fun App(){
//    MaterialTheme {
//        val screenWidth = getScreenWidth()
//        val screenHeight = getScreenHeight()
//
//        val spriteState = rememberSpriteState(
//            totalFrames = 4,
//            framesPerRow=2
//        )
//        val spriteSpec = remember{
//            SpriteSpec(
//                screenWidth=screenWidth.value,
//                default = SpriteSheet(
//                    frameWidth = 450,
//                    frameHeight = 450,
//                    image = Res.drawable.sprite_sheet
//
//                )
//            )
//        }
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ){
//            SpriteView(
//                spriteState=spriteState,
//                spriteSpec = spriteSpec
//            )
//        }
//    }
//}
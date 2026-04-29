package no.uio.ifi.in2000.team16.ui.onboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.R

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
//Just an example, need more sprites
    val pages = listOf(
        OnBoardModel(
            title = "Velkommen",
            description = "Sopp sporer er ett verktøy for å beregne om ulike områder" +
                    " har potensialet for sopp på en skala fra 1-100",
            imageRes = R.drawable.sprite01
        ),
        OnBoardModel(
            title = "",
            description = "Når du først åpner appen vil du se kartet. Der kan du trykke på en" +
                    " plass i karetet eller søke det opp og den vil automatisk beregne skoren",
            imageRes = R.drawable.sprite01
        ),
        OnBoardModel(
            title = "Menyen",
            description = "Oppe til venstre er det en liten meny. Inni den kan du skru på heatmap" +
                    "Den vil markere hele kartet i rødt, grønt eller gult basert på om områdene har " +
                    "mye eller lite potensiale for sopp. Rødt betyr lite, grønt er mye og gult er litt ",
            imageRes = R.drawable.sprite01
        ),
        OnBoardModel(
            title = "Historiske funn",
            description = "Denne lar deg se hvor det tidligere har blitt funnet sopp." +
                    "Denne informasjonen ble tatt fra artsdatabanken",
            imageRes = R.drawable.sprite01
        ),
        OnBoardModel(
            title = "Satelittmodus",
            description = "Her kan du enkelt endre kartmodus mellom sattelitt og standard kartmodus",
            imageRes = R.drawable.sprite01
        ),
        OnBoardModel(
            title = "Skoglag",
            description = "...",
            imageRes = R.drawable.sprite01
        ),
        OnBoardModel(
            title = "Soppsiden",
            description = "Soppsiden er en generell oversikt over de ulike soppene du kan finne ute." +
                    "Her ",
            imageRes = R.drawable.sprite01
        )

    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    val isLastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnBoardItem(pages[page])
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            /*🔹 Skip (skjules på siste side)
            Text(
                text = if (isLastPage) "" else "Skip",
                style = TextStyle(
                    color = Color(0xFFAAAAAA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier.clickable {
                    onFinished()
                }
            )

             */

            // 🔹 Dots indikator
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .width(if (isSelected) 18.dp else 8.dp)
                            .height(8.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF707784),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                color = if (isSelected) Color(0xFF3B6C64) else Color.White,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                }
            }

            // 🔹 Next / Finish
            Text(
                text = if (isLastPage) "Finish" else "Next",
                style = TextStyle(
                    color = Color(0xFF333333),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier.clickable {
                    if (!isLastPage) {
                        val nextPage = pagerState.currentPage + 1
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextPage)
                        }
                    } else {
                        onFinished()
                    }
                }
            )
        }
    }
}
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
/*
The onboardins screen shown when the user first opens the app.
It explains the main features of the app through multiple swipeable pages.
 */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
/*
The pages for the onboardingscreen, uses the OnBoardModel class with a title, desctiption and
Imageres, can add more if needed.
 */
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
            description = "Her kan du velge om du vil se på skoglagene i kartet eller ikke, fargene symboliserer hvilke tretype som dominerer området",
            imageRes = R.drawable.sprite01
        ),
        OnBoardModel(
            title = "Soppsiden",
            description = "Soppsiden er en generell oversikt over de ulike soppene du kan finne, om de er giftige og om de er en god matsopp." ,
            imageRes = R.drawable.sprite01
        )

    )
    //Keeps track of the current onboarding page
    val pagerState = rememberPagerState(pageCount = { pages.size })
    //Used to start the page animation when pressing Next
    val coroutineScope = rememberCoroutineScope()
    // Checks if the user is on the last page
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


            // Shows which page the user is currently on with a dot indikator
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

            // Moves to the next page or finishes onboarding if its already on the last page
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
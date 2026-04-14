package no.uio.ifi.in2000.team16.ui.onboard


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import no.uio.ifi.in2000.team16.R

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinished: ()-> Unit
) {
    val pages = listOf(
        OnBoardModel(
            title = "VELKOMMEN",
            description = "Faskjer",
            imageRes = R.drawable.ic_onboarding_1
        ),
        OnBoardModel(
            title = "HUSKELISTE",
            description = "Ha på ordentlige klær og riktig utstyr:)",
            imageRes = R.drawable.ic_onboarding_2
        ),
        OnBoardModel(
            title = "VIKTIG",
            description = "Soppesiden er ikke en fasit for om soppen er giftig eller ikke. Hvis du er usikker, ikke ta den med!",
            imageRes = R.drawable.ic_onboarding_3
        )
    )

    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )
    val coroutineScope = rememberCoroutineScope()

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

            Text(
                "Skip", style = TextStyle(
                    color = Color(0xFFAAAAAA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier.clickable {
                    val skipPage = pagerState.pageCount-1
                    coroutineScope.launch { pagerState.animateScrollToPage(skipPage) }
                }
            )

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
                            .height(if (isSelected) 8.dp else 8.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF707784),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                color = if (isSelected) Color(0xFF3B6C64) else Color(0xFFFFFFFF),
                                shape = CircleShape
                            )
                    )
                }
            }


            Text(
                "Next", style = TextStyle(
                    color = Color(0xFF333333),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier.clickable {
                    if (pagerState.currentPage < pages.lastIndex) {
                        val nextPage = pagerState.currentPage + 1
                        coroutineScope.launch { pagerState.animateScrollToPage(nextPage) }
                    } else {
                        onFinished()
                    }

                }
            )

        }
    }
}
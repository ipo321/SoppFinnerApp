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

    val pages = listOf(
        OnBoardModel(
            title = "Velkommennnn",
            description = "Faskjer",
            imageRes = R.drawable.ic_onboarding_1
        ),
        OnBoardModel(
            title = "Huskeliste",
            description = "blablabla",
            imageRes = R.drawable.ic_onboarding_2
        ),
        OnBoardModel(
            title = "VIKTIG",
            description = "Ikke dø",
            imageRes = R.drawable.warning
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

            // 🔹 Skip (skjules på siste side)
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
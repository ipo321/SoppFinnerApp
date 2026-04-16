package no.uio.ifi.in2000.team16.ui.onboard

import androidx.annotation.DrawableRes
import no.uio.ifi.in2000.team16.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    object First : OnBoardingPage(
        image = R.drawable.ic_onboarding_1,
        title = "VELKOMMEN",
        description = "FASKJER.",
    )

    object Second : OnBoardingPage(
        image = R.drawable.ic_onboarding_2,
        title = "HUSKELISTE",
        description = "Fblablalbalabla"
    )

    object Third : OnBoardingPage(
        image = R.drawable.warning,
        title = "VIKTIG",
        description = "ikke dø"
    )
}
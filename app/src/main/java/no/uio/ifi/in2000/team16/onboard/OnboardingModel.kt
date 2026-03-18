package no.uio.ifi.in2000.team16.onboard

import androidx.annotation.DrawableRes

sealed class OnboardingModel {(
    @DrawableRes
    val image: Int,
            val title: String,
                    val description: String,
    )
}
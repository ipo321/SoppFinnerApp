package no.uio.ifi.in2000.team16

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Team16App : Application() {
}

//Når navnet endres, huske å endre navn i manifestet: android:name=".Team16App"
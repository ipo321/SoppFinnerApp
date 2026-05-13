package no.uio.ifi.in2000.team16

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import no.uio.ifi.in2000.team16.ui.popUp.CrashHandler

/**
    Necessary class for HiltViewModel.
 */

@HiltAndroidApp
class Team16App : Application() {
    override fun onCreate() {
        super.onCreate()
        org.maplibre.android.MapLibre.getInstance(this)

        //Unexpected crashes handler
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(applicationContext))
    }
}

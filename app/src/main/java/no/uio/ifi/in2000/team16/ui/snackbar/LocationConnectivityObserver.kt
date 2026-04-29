package no.uio.ifi.in2000.team16.ui.snackbar

import android.content.Context
import android.location.LocationManager
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.launch

fun observeLocationAvailability(context: Context): Flow<Boolean> = callbackFlow {

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun isEnabled() = LocationManagerCompat.isLocationEnabled(locationManager)

    trySend(isEnabled())

    val job = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch{
        while(true){
            trySend(isEnabled())
            kotlinx.coroutines.delay(2000)
        }
    }

    awaitClose{job.cancel()}
}

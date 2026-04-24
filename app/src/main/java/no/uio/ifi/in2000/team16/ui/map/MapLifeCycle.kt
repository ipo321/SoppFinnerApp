package no.uio.ifi.in2000.team16.ui.map

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.maps.MapView

/*
Life cycle for the map screen
 */


@Composable
fun MapLifecycle(mapView: MapView, isVisible: Boolean) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner, mapView, isVisible) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                Lifecycle.Event.ON_RESUME -> if (isVisible) mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> if (isVisible) mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        // Ensure correct state if visibility changes while Activity is already RESUMED
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            if (isVisible) mapView.onResume() else mapView.onPause()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Handle Low Memory events to prevent native crashes
    DisposableEffect(context, mapView) {
        val callback = object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {}

            @Suppress("OVERRIDE_DEPRECATION")   //Overriding the warning onLowMemory since we have no other way
            override fun onLowMemory() {
                mapView.onLowMemory()
            }
            override fun onTrimMemory(level: Int) {}
        }
        context.registerComponentCallbacks(callback)
        onDispose {
            context.unregisterComponentCallbacks(callback)
        }
    }
}

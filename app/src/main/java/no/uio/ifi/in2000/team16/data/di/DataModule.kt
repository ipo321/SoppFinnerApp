package no.uio.ifi.in2000.team16.data.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext

/*
    Object creates an Http client and the Storage module
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mushroom_prefs")

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        //adds a logging of all network traffic to Logcat under T16_Network
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("T16_Network_Call", message)
                }
            }
            // Gives URL, HTTP-status.
            // Change to LogLevel.ALL if u wanna debug the whole response.
            level = LogLevel.INFO
        }
    }.apply {
            // Give time used for network communication
            plugin(HttpSend).intercept { request ->
                val start = System.currentTimeMillis()
                val call = execute(request)
                Log.d("T16_Network_Time", "Call tok ${System.currentTimeMillis() - start} ms")
                call
            }
    }
    // DataStore provide
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
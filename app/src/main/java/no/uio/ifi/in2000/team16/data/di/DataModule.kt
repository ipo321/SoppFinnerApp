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
import dagger.Binds
import dagger.hilt.android.qualifiers.ApplicationContext
import no.uio.ifi.in2000.team16.data.ai.AiDataSource
import no.uio.ifi.in2000.team16.data.ai.AiDataSourceInterface
import no.uio.ifi.in2000.team16.data.artsdatabasen.LocalMushroomDataSource
import no.uio.ifi.in2000.team16.data.artsdatabasen.LocalMushroomDataSourceInterface
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepository
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepositoryInterface
import no.uio.ifi.in2000.team16.data.kartverket.AddressDataSource
import no.uio.ifi.in2000.team16.data.kartverket.AddressDataSourceInterface
import no.uio.ifi.in2000.team16.data.kartverket.LocationRepository
import no.uio.ifi.in2000.team16.data.kartverket.LocationRepositoryInterface
import no.uio.ifi.in2000.team16.data.kartverket.PlaceDataSource
import no.uio.ifi.in2000.team16.data.kartverket.PlaceDataSourceInterface
import no.uio.ifi.in2000.team16.data.met.frost.FrostDataSource
import no.uio.ifi.in2000.team16.data.met.frost.FrostDataSourceInterface
import no.uio.ifi.in2000.team16.data.met.frost.FrostRepository
import no.uio.ifi.in2000.team16.data.met.frost.FrostRepositoryInterface
import no.uio.ifi.in2000.team16.data.met.locationForecast.LocationForecastDataSource
import no.uio.ifi.in2000.team16.data.met.locationForecast.LocationForecastDataSourceInterface
import no.uio.ifi.in2000.team16.data.met.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.team16.data.met.locationForecast.LocationForecastRepositoryInterface
import no.uio.ifi.in2000.team16.data.nibio.ForestDataSource
import no.uio.ifi.in2000.team16.data.nibio.ForestDataSourceInterface
import no.uio.ifi.in2000.team16.data.nibio.ForestRepository
import no.uio.ifi.in2000.team16.data.nibio.ForestRepositoryInterface
import no.uio.ifi.in2000.team16.data.nibio.SatSkogDataSource
import no.uio.ifi.in2000.team16.data.nibio.SatSkogDataSourceInterface
import no.uio.ifi.in2000.team16.data.nibio.SatSkogRepository
import no.uio.ifi.in2000.team16.data.nibio.SatSkogRepositoryInterface
import no.uio.ifi.in2000.team16.data.onBoard.DataStoreRepository
import no.uio.ifi.in2000.team16.data.onBoard.DataStoreRepositoryInterface
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSource
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSourceInterface
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.data.species.SpeciesRepositoryInterface

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
            level = LogLevel.INFO  //INFO
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

/*
    The BindModule tells Hilt which classes that should be provided from which interface
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {

    @Binds
    @Singleton
    abstract fun bindForestDataSource(
        impl: ForestDataSource
    ): ForestDataSourceInterface

    @Binds
    @Singleton
    abstract fun bindForestRepository(
        impl: ForestRepository
    ): ForestRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindSatSkogDataSource(
        impl: SatSkogDataSource
    ): SatSkogDataSourceInterface

    @Binds
    @Singleton
    abstract fun bindSatSkogRepository(
        impl: SatSkogRepository
    ): SatSkogRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindFrostDataSource(
        impl: FrostDataSource
    ): FrostDataSourceInterface

    @Binds
    @Singleton
    abstract fun bindFrostRepository(
        impl: FrostRepository
    ): FrostRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindLocationForecastDataSource(
        impl: LocationForecastDataSource
    ): LocationForecastDataSourceInterface

    @Binds
    @Singleton
    abstract fun bindLocationForecastRepository(
        impl: LocationForecastRepository
    ): LocationForecastRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindAiDataSource(
        impl: AiDataSource
    ): AiDataSourceInterface

    @Binds
    @Singleton
    abstract fun bindLocalMushroomDataSource(
        impl: LocalMushroomDataSource
    ): LocalMushroomDataSourceInterface

    @Binds
    @Singleton
    abstract fun bindMushroomRepository(
        impl: MushroomRepository
    ): MushroomRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindAddressDataSource(
        impl: AddressDataSource
    ): AddressDataSourceInterface

    @Binds
    @Singleton
    abstract fun bindPlaceDataSource(
        impl: PlaceDataSource
    ): PlaceDataSourceInterface

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepository
    ): LocationRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(
        impl: DataStoreRepository
    ): DataStoreRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindSpeciesDataSource(
        impl: SpeciesDataSource
    ): SpeciesDataSourceInterface

    @Binds
    @Singleton
    abstract fun bindSpeciesRepository(
        impl: SpeciesRepository
    ): SpeciesRepositoryInterface
}
package no.uio.ifi.in2000.team16.domain.usecase

import android.util.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import no.uio.ifi.in2000.team16.data.met.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepository
import no.uio.ifi.in2000.team16.data.met.frost.FrostRepository
import no.uio.ifi.in2000.team16.data.nibio.ForestRepository
import no.uio.ifi.in2000.team16.data.nibio.SatSkogRepository
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import javax.inject.Inject
import java.util.Calendar
import kotlin.system.measureTimeMillis


/*
Handles a single UseCase, takes in lon and lat and then calls all of the different
data repositories to fetch the data. Simplifies the Viewmodel. Returns the environment context object
*/

class GetEnvironmentContextUseCase @Inject constructor(
    private val locationForecastRepository: LocationForecastRepository,
    private val mushroomRepository: MushroomRepository,
    private val forestRepository: SatSkogRepository,
    private val frostRepository: FrostRepository,
    private val ar5Repository: ForestRepository
    //Add other repositories here
) {
    suspend operator fun invoke(lat: Double, lon: Double, profile: SpeciesProfile? = null): EnvironmentContext {
        Log.d("T16_UC_EnvironmentContext", "Fetching environment context for lat: $lat, lon: $lon")

        var context: EnvironmentContext? = null

        val timeTaken = measureTimeMillis {
            try {
                context = coroutineScope {
                    val weather = async { locationForecastRepository.weatherNext8Hours(lat, lon) }
                    val forest = async { forestRepository.getForestData(lat, lon) }
                    val history = async { frostRepository.weatherHistory(lat, lon) }
                    val AR5 = async { ar5Repository.getArealType(lat, lon) }
                    //Add other repositories here

                    //Only send if we get species or else null, makes it possible to use the function for all species
                    val speciesLocations = if (profile != null) {
                        async {
                            mushroomRepository.getMushroomsNearLocation(
                                lat,
                                lon,
                                mushRoomType = profile.name
                            )
                        }
                    } else {
                        null // Only send if we get species or else null
                    }

                    val weatherData = weather.await()
                    val forestData = forest.await()
                    val historyData = history.await()

                    val occurrenceResult = speciesLocations?.await()
                    val historicalFindCount = occurrenceResult?.count ?: 0
                    val historicalFinds = occurrenceResult?.results ?: emptyList()

                    val forestAR5 = AR5.await()
                    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

                    EnvironmentContext(
                        currentWeather = weatherData,
                        weatherHistory = historyData,
                        terrainData = null,
                        ar5 = forestAR5,
                        forestData = forestData,
                        historicalFindCount = historicalFindCount,
                        historicalFinds = historicalFinds,
                        currentMonth = currentMonth
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    "T16_UC_EnvironmentContext",
                    "Error fetching environment context: ${e.message}",
                    e
                )
                throw e

            }

        }
        //Log code for debugging
        val results = context!!
        val hasWeather = results.currentWeather != null
        val hasForest = results.forestData != null
        val hasAr5 = results.ar5 != null
        val hasHistory = results.weatherHistory != null

        if (hasWeather && hasForest && hasAr5 && hasHistory) {
            Log.i("T16_UC_EnvironmentContext", "Context fetched in $timeTaken ms")
        } else {
            Log.w(
                "T16_UC_EnvironmentContext", "Context fetch in $timeTaken ms, but missing data. Vær: $hasWeather, Skog: $hasForest, AR5: $hasAr5\" History: $hasHistory "
            )
        }

        return results
    }
}
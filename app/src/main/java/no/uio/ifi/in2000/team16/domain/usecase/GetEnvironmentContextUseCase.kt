package no.uio.ifi.in2000.team16.domain.usecase

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepositoryInterface
import no.uio.ifi.in2000.team16.data.met.frost.FrostRepositoryInterface
import no.uio.ifi.in2000.team16.data.met.locationForecast.LocationForecastRepositoryInterface
import no.uio.ifi.in2000.team16.data.nibio.ForestRepositoryInterface
import no.uio.ifi.in2000.team16.data.nibio.SatSkogRepositoryInterface
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import java.util.Calendar
import javax.inject.Inject
import kotlin.system.measureTimeMillis


/*
Handles a single UseCase, takes in lon and lat and then calls all the different
data repositories to fetch the data. Simplifies the Viewmodel. Returns the environment context object
*/

class GetEnvironmentContextUseCase @Inject constructor(
    private val locationForecastRepository: LocationForecastRepositoryInterface,
    private val mushroomRepository: MushroomRepositoryInterface,
    private val forestRepository: SatSkogRepositoryInterface,
    private val frostRepository: FrostRepositoryInterface,
    private val ar5Repository: ForestRepositoryInterface
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
                    val arealresourcetype = async { ar5Repository.getArealType(lat, lon) }
                    //Add other repositories here

                    //Only send if we get species or else null, makes it possible to use the function for all species
                    val speciesLocations = if (profile != null) {
                        async {
                            mushroomRepository.getMushroomsNearLocation(
                                lat,
                                lon,
                                mushroomType = profile.name
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

                    val forestAR5 = arealresourcetype.await()
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
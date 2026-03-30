package no.uio.ifi.in2000.team16.domain.usecase

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

        return coroutineScope {
            val weather = async { locationForecastRepository.weatherNext8Hours(lat, lon) }
            val forest = async { forestRepository.getForestData(lat, lon) }
            val history = async { frostRepository.weatherHistory(lat, lon) }
            val AR5 = async { ar5Repository.getArealType(lat, lon) }


            //Add other repositories here
            //val frost = async { frostRepository.getFrostInfo(lat, lon) }


            //Only send if we get species or else null, makes it possible to use the function for all species
            val speciesCount = if (profile != null) {
                async { mushroomRepository.getSpeciesCountNearLocation(lat, lon, profile.taxKey) }
            } else {
                null // Only send if we get species or else null
            }

            val weatherData = weather.await()
            val forestData = forest.await()
            val historyData = history.await()
            val speciesData = speciesCount?.await() ?: 0
            val forestAR5 = AR5.await()
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1


            EnvironmentContext(
                currentWeather = weatherData,
                weatherHistory = historyData,
                terrainData = null,
                ar5 = forestAR5,
                forestData = forestData,
                historicalFinds = speciesData,
                currentMonth = currentMonth
            )
        }
    }
}
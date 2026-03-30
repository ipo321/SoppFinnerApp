package no.uio.ifi.in2000.team16.domain.usecase

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomSpecies
import no.uio.ifi.in2000.team16.data.met.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepository
import no.uio.ifi.in2000.team16.data.met.frost.FrostRepository
import no.uio.ifi.in2000.team16.data.nibio.SatSkogRepository
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
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
    private val frostRepository: FrostRepository
    //Add other repositories here
) {
    suspend operator fun invoke(lat: Double, lon: Double, species: MushroomSpecies? = null): EnvironmentContext {

        return coroutineScope {
            val weather = async { locationForecastRepository.weatherNext8Hours(lat, lon) }
            val forest = async { forestRepository.getForestData(lat, lon) }
            val history = async { frostRepository.weatherHistory(lat, lon) }

            //Add other repositories here
            //val frost = async { frostRepository.getFrostInfo(lat, lon) }


            //Only send if we get species or else null, makes it possible to use the function for all species
            val speciesCount = if (species != null) {
                async { mushroomRepository.getSpeciesCountNearLocation(lat, lon, species) }
            } else {
                null // Only send if we get species or else null
            }

            val weatherData = weather.await()
            val forestData = forest.await()
            val historyData = history.await()
            val speciesData = speciesCount?.await() ?: 0
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1


            EnvironmentContext(
                currentWeather = weatherData,
                weatherHistory = historyData,
                terrainData = null,
                forestData = forestData,
                historicalFinds = speciesData,
                currentMonth = currentMonth
            )
        }
    }
}
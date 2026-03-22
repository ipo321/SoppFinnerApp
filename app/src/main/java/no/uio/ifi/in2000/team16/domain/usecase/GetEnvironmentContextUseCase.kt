package no.uio.ifi.in2000.team16.domain.usecase

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import no.uio.ifi.in2000.team16.data.met.LocationForecastRepository
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import javax.inject.Inject
import java.util.Calendar


/*
Handles a single UseCase, takes in lon and lat and then calls all of the different
data repositories to fetch the data. Simplifies the Viewmodel. Returns the environment context object
*/

class GetEnvironmentContextUseCase @Inject constructor(
    private val locationForecastRepository: LocationForecastRepository
    //Add other repositories here
    //private val mushroomRepository: MushroomRepository
    //private val forestRepository: ForestRepository
    //private val frostRepository: FrostRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): EnvironmentContext {
        return coroutineScope {
            val weather = async { locationForecastRepository.weatherNext8Hours(lat, lon) }
            //Add other repositories here

            val weatherData = weather.await()
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1


            EnvironmentContext(
                currentWeather = weatherData,
                weatherHistory = null,
                terrainData = null,
                forestData = null,
                historicalFinds = 0,
                currentMonth = currentMonth
            )
        }
    }
}
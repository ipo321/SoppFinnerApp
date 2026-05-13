package no.uio.ifi.in2000.team16.data.kartverket

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

interface LocationRepositoryInterface {

    suspend fun search(
        query: String
    ): List<SearchableLocation>

    suspend fun getLocationCoordinates(
        query: String
    ): SearchableLocation?
}

class LocationRepository @Inject constructor(
    private val addressDataSource: AddressDataSourceInterface,
    private val placeDataSource: PlaceDataSourceInterface
) : LocationRepositoryInterface {
    override suspend fun search(query: String): List<SearchableLocation>  = coroutineScope {
        val addressSearch = async { addressDataSource.suggest(query) }
        val placeSearch = async { placeDataSource.suggest(query) }
        val addresses = try { addressSearch.await() } catch (e: Exception) {
            Log.e("AddressDataSource", "Error in geocode: ${e.message}")
            emptyList() }
        val places = try { placeSearch.await() } catch (e: Exception) {
            Log.e("PlaceDataSource", "Error in geocode: ${e.message}")
            emptyList() }

        (places + addresses) // Combines and returns results as one list
            .filter { it.latitude != null && it.longitude != null } // Removes results without lat and lon
            .distinctBy { it.title.lowercase() }
            .sortedWith(compareByDescending<SearchableLocation> {
                if (it is Place) {
                    when (it.navneobjekttype?.lowercase()) {
                        "fylke" -> 3 // Priorities "fylke" over "kommune"
                        "kommune" -> 2
                        else -> 1
                    }
                }
                else 0
                }.thenBy { it.title.length }) // Shortest name comes first in suggestions
            .take(10)
    }

    override suspend fun getLocationCoordinates(query: String): SearchableLocation? {
        return search(query).firstOrNull()
    }
}

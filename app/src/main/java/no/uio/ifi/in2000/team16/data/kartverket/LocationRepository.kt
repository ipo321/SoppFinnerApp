package no.uio.ifi.in2000.team16.data.kartverket

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val addressDataSource: AddressDataSource,
    private val placeDataSource: PlaceDataSource
) {
    suspend fun search(query: String): List<SearchableLocation>  = coroutineScope {

        val addressSearch = async { addressDataSource.suggest(query) }
        val placeSearch = async { placeDataSource.suggest(query) }
        val addresses = try { addressSearch.await() } catch (e: Exception) {
            Log.e("AddressDataSource", "Error in geocode: ${e.message}")
            emptyList() }
        val places = try { placeSearch.await() } catch (e: Exception) {
            Log.e("PlaceDataSource", "Error in geocode: ${e.message}")
            emptyList() }

        (addresses + places) // Combines and returns results as one list
            .filter { it.latitude != null && it.longitude != null } // Removes results without lat and lon
            .distinctBy { it.title.lowercase() }
            .take(10)
    }

    suspend fun getLocationCoordinates(query: String): SearchableLocation? {
        return search(query).firstOrNull()
    }
}

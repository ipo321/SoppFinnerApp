package no.uio.ifi.in2000.team16.data.kartverket

import android.util.Log
import javax.inject.Inject

class PlaceRepository @Inject constructor(
    private val placeDataSource: PlaceDataSource
) {
    suspend fun geocodeSearch(query: String): Place? {
        return try {
            val response = placeDataSource.suggest(query)
            response.firstOrNull()
        } catch (e: Exception) {
            Log.e("PlaceDataSource", "Error in geocode: ${e.message}")
            null
        }
    }
    suspend fun getLocationCoordinates(query: String): RepresentationPoint? {
        return geocodeSearch(query)?.representasjonspunkt
    }

    suspend fun getSuggestions(query: String): List<Place> {
        return placeDataSource.suggest(query)
    }
}


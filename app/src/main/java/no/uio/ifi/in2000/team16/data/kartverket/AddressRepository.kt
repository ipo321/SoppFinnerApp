package no.uio.ifi.in2000.team16.data.kartverket

import android.util.Log
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val addressDataSource: AddressDataSource
) {
    suspend fun geocodeSearch(query: String): Address? {
        return try {
            val response = addressDataSource.suggest(query)
            response.firstOrNull()
        } catch (e: Exception) {
            Log.e("ArcGISDataSource", "Error in geocode: ${e.message}")
            null
        }
    }
    suspend fun getLocationCoordinates(query: String): RepresentationPoint? {
        return geocodeSearch(query)?.representasjonspunkt
    }

    suspend fun getSuggestions(query: String): List<Address> {
        return addressDataSource.suggest(query)
    }
}

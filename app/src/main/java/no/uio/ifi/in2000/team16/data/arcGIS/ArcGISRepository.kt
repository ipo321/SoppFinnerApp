package no.uio.ifi.in2000.team16.data.arcGIS

import javax.inject.Inject

class ArcGISRepository @Inject constructor(
    private val arcGISDataSource: ArcGISDataSource
) {
    suspend fun getLocationCoordinates(query: String): Location? {
        return arcGISDataSource.geocodeSearch(query)?.location
    }

    suspend fun getSuggestions(query: String): List<Suggestion> {
        return arcGISDataSource.suggest(query)
    }
}
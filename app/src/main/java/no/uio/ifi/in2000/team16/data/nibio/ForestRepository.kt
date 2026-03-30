package no.uio.ifi.in2000.team16.data.nibio

import javax.inject.Inject
import javax.inject.Singleton

/*
    Uses the same method as SAT-SKOG
 */
@Singleton
class ForestRepository @Inject constructor(
    private val forestDataSource: ForestDataSource
) {
    suspend fun getArealType(lat: Double, lon: Double): String? {
        return forestDataSource.fetchArealType(lat, lon)
    }
}
package no.uio.ifi.in2000.team16.data.nibio

import javax.inject.Inject
import javax.inject.Singleton
import no.uio.ifi.in2000.team16.domain.model.ForestData


/*
   Repository for the SAT-SKOG Api than handles the data output and puts it into a ForestData module from the evaluation module
*/

@Singleton
class SatSkogRepository @Inject constructor(
    private val dataSource: SatSkogDataSource
){
    suspend fun getForestData(lat: Double, lon: Double): ForestData? {

        val response = dataSource.fetchForest(lat, lon) ?: return null

        return ForestData(
            dominantTreeSpecies = response.treeSpecies,
            sprucePercentage = response.sprucePct,
            pinePercentage = response.pinePct,
            broadleafPercentage = response.broadleafPct,
            fertilityCategory = response.fertilityCategory,
            siteIndex = response.siteIndex,
            standAge = response.standAge,
            minElevation = response.minElevation,
            maxElevation = response.maxElevation
        )
    }
}

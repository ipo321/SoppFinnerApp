package no.uio.ifi.in2000.team16.data.MushroomData

import jakarta.inject.Inject
import kotlin.math.cos

class MushroomRepository @Inject constructor(
    private val mushroomDataSource: MushroomDataSource
) {

    suspend fun searchMushrooms(query: String): List<MushroomInfo> =
        mushroomDataSource.searchMushrooms(query).map { it.toMushroom() }


    suspend fun getMushroomDetails(id: String): MushroomInfo? =
        mushroomDataSource.getMushroomById(id)?.toMushroom()

    suspend fun getLocationCount(
        lat: Double,
        lon: Double,
        radiusKm: Double = 10.0,
        taxonKey: String = "all mushrooms",
    ): Int {
        val latOffset = radiusKm / 111.0
        val lonOffset = radiusKm / (111.0 * cos(Math.toRadians(lat)))//recalculation for the variation of latidiudes

        //making a square box around the location
        val latMin = lat - latOffset
        val latMax = lat + latOffset
        val lonMin = lon - lonOffset
        val lonMax = lon + lonOffset


        return mushroomDataSource.getMushroomCountNearLocation(taxonKey, latMin, latMax, lonMin, lonMax)
    }


    // Convert Species to MushroomInfo
    private fun Species.toMushroom(): MushroomInfo =
        MushroomInfo(
            id = key.toString(),
            scientificName = scientificName,
            commonName = vernacularNames
                .firstOrNull { it.language == "nob" }?.vernacularName
                ?: vernacularNames.firstOrNull { it.language == "nno" }?.vernacularName
                ?: vernacularNames.firstOrNull()?.vernacularName,
            kingdom = kingdom,
            phylum = phylum,
            klass = `class`,
            order = order,
            family = family,
            genus = genus,
            species = species
        )
}
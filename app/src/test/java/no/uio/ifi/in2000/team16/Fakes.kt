package no.uio.ifi.in2000.team16

import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepositoryInterface
import no.uio.ifi.in2000.team16.data.artsdatabasen.OccurrenceResult
import no.uio.ifi.in2000.team16.data.kartverket.LocationRepositoryInterface
import no.uio.ifi.in2000.team16.data.kartverket.SearchableLocation
import no.uio.ifi.in2000.team16.data.species.SpeciesRepositoryInterface
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile

/**
 * A fake SpeciesRepository used for unit tests.
 */
class FakeSpeciesRepository : SpeciesRepositoryInterface {
    private val allMushrooms = mutableListOf( // Two fake mushrooms
        Mushroom("Kantarell", "", R.drawable.cute_mushroom, "", emptyList(), mockk<SpeciesProfile>(relaxed = true)),
        Mushroom("Steinsopp", "", R.drawable.cute_mushroom, "", emptyList(), mockk<SpeciesProfile>(relaxed = true))
    )
    override val selectedMushroom = MutableStateFlow(allMushrooms[0])
    override val favoriteMushrooms = MutableStateFlow<List<Mushroom>>(emptyList())

    override fun getAllMushrooms(): List<Mushroom> = allMushrooms
    override fun getMushroomCount(): Int = 2
    override fun getMushroomByName(name: String): Mushroom? = allMushrooms.firstOrNull { it.commonName == name }
    override suspend fun initializeDefaults() { favoriteMushrooms.value = allMushrooms.take(2) }
    override suspend fun updateSelectedMushroom(mushroom: Mushroom) { selectedMushroom.value = mushroom }
    override suspend fun toggleFavorite(mushroom: Mushroom) {
        val current = favoriteMushrooms.value.toMutableList()
        if (current.contains(mushroom)) current.remove(mushroom) else current.add(mushroom)
        favoriteMushrooms.value = current
    }
}

/**
 * A fake LocationRepository used for unit tests.
 */
class FakeLocationRepository : LocationRepositoryInterface {
    var searchResults: Map<String, List<SearchableLocation>> = emptyMap()
    var coordinatesResults: Map<String, SearchableLocation?> = emptyMap()

    override suspend fun search(query: String): List<SearchableLocation> = searchResults[query] ?: emptyList()
    override suspend fun getLocationCoordinates(query: String): SearchableLocation? = coordinatesResults[query]
}

/**
 * A fake MushroomRepository used for unit tests.
 */
@Suppress("unused")
class FakeMushroomRepository : MushroomRepositoryInterface {
    val occurrence = mockk<OccurrenceResult>(relaxed = true)

    override val defaultKmRadius: Double = 10000000.0

    override suspend fun getMushroomsNearLocation(
        lat: Double,
        lon: Double,
        radiusKm: Double,
        mushroomType: String,
    ): OccurrenceResult = occurrence

    override suspend fun getMushroomInBoundingBox(
        north: Double,
        south: Double,
        east: Double,
        west: Double,
        mushroomType: String,
    ): OccurrenceResult = occurrence
}


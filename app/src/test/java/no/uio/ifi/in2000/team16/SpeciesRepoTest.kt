package no.uio.ifi.in2000.team16

import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSource
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * A test for SpeciesRepository.
 * ----------------------------
 * Requirements:
 *  - getMushroomByName() must return the right mushroom name
 *  - An invalid name as parameter must return the default "Kantarell"
 *  - The repository must be able to toggle favorites
 */

class SpeciesRepoTest {
    // In this test we need to mock the repository and not use the fake one, to be able to get the
    // correct result from the data source for wrong name search.
    private val speciesDS = SpeciesDataSource()
    private val mockRepo = SpeciesRepository(speciesDS, mockk(relaxed = true))
    private val fakeRepo = FakeSpeciesRepository()

    @Test
    fun testReturnWithKantarell(){
        val specie = fakeRepo.getMushroomByName("Kantarell")
        assertEquals("Kantarell", specie?.commonName)
    }

    @Test
    fun testReturnWithSteinsopp(){
        val species = fakeRepo.getMushroomByName("Steinsopp")
        assertEquals("Steinsopp", species?.commonName)
    }

    @Test
    fun wrongNameSearch() {
        val species = mockRepo.getMushroomByName("wrong")
        assertEquals("Kantarell", species?.commonName)
    }

    @Test
    fun testGetAllSpecies() {
        val allSpecies = fakeRepo.getAllMushrooms()
        assertEquals(2, allSpecies.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun toggleFavorites() = runTest {
        fakeRepo.initializeDefaults()

        val initialFavorites = fakeRepo.favoriteMushrooms.first()
        val favoritesSize = initialFavorites.size
        val mushroom = fakeRepo.getMushroomByName("Kantarell")

        fakeRepo.toggleFavorite(mushroom!!) // Removes this mushroom

        val updatedFavorites = fakeRepo.favoriteMushrooms.first()

        // The size of favorites should now be one less
        assertEquals(favoritesSize - 1, updatedFavorites.size)
        // The removed mushroom should not be in favorites
        assertFalse(updatedFavorites.contains(mushroom))
    }
}


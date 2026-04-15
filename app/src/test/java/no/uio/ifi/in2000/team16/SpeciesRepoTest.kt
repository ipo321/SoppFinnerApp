package no.uio.ifi.in2000.team16

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSource
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


// Will return the species by name
// Returns Kantarell (first object) object if the name is wrong
// It can get a list of all species


class SpeciesRepoTest {
    private val speciesDS = SpeciesDataSource()
    private val mockDataStore = mockk<DataStore<Preferences>>(relaxed = true)

    private lateinit var repo: SpeciesRepository

    // Setup before tests run
    @Before
    fun setup() {
        every { mockDataStore.data } returns flowOf(preferencesOf())
        repo = SpeciesRepository(speciesDS, mockDataStore)
    }

    @Test
    fun testReturnWithKantarell() {
        val specie = repo.getMushroomByName("Kantarell")
        assertEquals("Kantarell", specie.commonName)
    }

    @Test
    fun testReturnWithSteinsopp() {
        val species = repo.getMushroomByName("Steinsopp")
        assertEquals("Steinsopp", species.commonName)
    }

    @Test
    fun wrongNameSearch() {
        val species = repo.getMushroomByName("wrong")
        assertEquals("Kantarell", species.commonName)
    }

    @Test
    fun testGetAllSpecies() {
        val allSpecies = repo.getAllMushrooms()
        assertEquals(3, allSpecies.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun toggleFavorites() = runTest {
        coEvery { mockDataStore.updateData(any()) } returns preferencesOf()
        // New repo for this test to make sure the mock-setup above is ready before init starts
        // We can refactor this test later on, but we need to refactor SpeciesRepositories Dispatcher in init
        // to do it, and that is not allowed in the team activity :(
        val favoritesRepo = SpeciesRepository(speciesDS, mockDataStore)
        
        // Makes sure the repo has added the three mushrooms
        favoritesRepo.favoriteMushrooms.first { it.isNotEmpty() }

        val favoritesSize = favoritesRepo.favoriteMushrooms.value.size
        val mushroom = favoritesRepo.getMushroomByName("Kantarell")

        favoritesRepo.toggleFavorite(mushroom) // Removes this mushroom

        // The size of favorites should now be one less
        assertEquals(favoritesSize - 1, favoritesRepo.favoriteMushrooms.value.size)
    }
}

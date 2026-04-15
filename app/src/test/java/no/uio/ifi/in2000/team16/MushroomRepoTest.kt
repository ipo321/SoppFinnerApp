package no.uio.ifi.in2000.team16

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomDataSource
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepository
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomSearchResult
import no.uio.ifi.in2000.team16.data.artsdatabasen.Occurrence
import no.uio.ifi.in2000.team16.data.artsdatabasen.OccurrenceResult
import no.uio.ifi.in2000.team16.data.artsdatabasen.VernacularName
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/*
    Test for MushroomRepository. Requirements:
    - Must return the right occurrences of mushrooms, and count these correctly
    - Must pick the right, preferably nob, name for a mushroom
    - Must not crash when the API returns null
    - Must calculate the correct bounding box
 */

class MushroomRepoTest {
    val mockDataSource = mockk<MushroomDataSource>()
    val repo = MushroomRepository(mockDataSource)

    // The repository should get the right mushroom data and count
    @Test
    fun testGetSpeciesAndMushroomsNearLocation() = runTest {
        val result = OccurrenceResult(count = 10, listOf(mockk<Occurrence>(), mockk<Occurrence>()))
        // Makes sure every call to getOccurrencesNearLocation returns result variable
        coEvery { mockDataSource.getOccurrencesNearLocation(any(), any(),
                any(), any(), any(), any()) } returns result
        val count = repo.getSpeciesCountNearLocation(59.0, 10.0, "34")
        val species = repo.getMushroomsNearLocation(59.0, 10.0)
        // The count from getSpeciesCountNearLocation() should be the same as returned by getOccurrencesNearLocation()
        assertEquals(10, count)
        // The occurrences from getMushroomsNearLocation() should be the same as returned by getOccurrencesNearLocation()
        assertEquals(result, species)
    }

    // The repository should pick the right, preferably nob, name
    @Test
    fun picksTheRightName() = runTest {
        val mockResult = MushroomSearchResult(
            key = 123,
            scientificName = "Cantharellus cibarius",
            vernacularNames = listOf(
                VernacularName("Chanterelle", "eng"),
                VernacularName("Kantarell", "nob")
            ),
            kingdom = "Fungi",
            phylum = "",
            `class` = "",
            order = "",
            family = "",
            genus = "",
            genusKey = 1,
            species = ""
        )

        coEvery { mockDataSource.searchMushrooms("Kantarell") } returns listOf(mockResult)
        val results = repo.searchMushrooms("Kantarell")

        // Should only find the one mockResult
        assertEquals(1, results.size)
        // The id should be the same as for mockResult
        assertEquals("123", results[0].id)
        // The commonName should be "Kantarell", tests the logic for picking the right name
        assertEquals("Kantarell", results[0].commonName)
    }

    // The repository should not crash when the API returns null
    @Test
    fun noMushroomDetailsReturnsNull() = runTest {
        coEvery { mockDataSource.getMushroomById("Unknown") } returns null
        val result = repo.getMushroomDetails("Unknown")
        // A search for "Unknown" should return null and not crash the repository
        assertNull(result)
    }

    // The repository should calculate the correct bounding box
    @Test
    fun calculatesCorrectBoundingBox() = runTest {
        val result = OccurrenceResult(count = 0, results = emptyList())
        coEvery { mockDataSource.getOccurrencesNearLocation(any(), any(),
                any(), any(), any(), any()) } returns result
        val lat = 59.0
        val lon = 10.0

        repo.getMushroomsNearLocation(lat, lon)

        // Verifies that the bounding box is calculated correctly
        // less and more tests that the result is in the correct range
        coVerify {
            mockDataSource.getOccurrencesNearLocation(
                any(),
                less(59.0),
                more(59.0),
                less(10.0),
                more(10.0),
                any()
            )
        }
    }
}

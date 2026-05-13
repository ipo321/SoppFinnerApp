package no.uio.ifi.in2000.team16

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team16.data.artsdatabasen.LocalMushroomDataSource
import no.uio.ifi.in2000.team16.data.artsdatabasen.LocalSpeciesIdMapper
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepository
import no.uio.ifi.in2000.team16.data.artsdatabasen.OccurrenceResult
import org.junit.Test
import kotlin.test.assertEquals


/**
    Test for MushroomRepository. Requirements:
    - Must return the right occurrences of mushrooms, and count these correctly
    - Must pick the right, preferably nob, name for a mushroom
    - Must not crash when the API returns null
    - Must calculate the correct bounding box
 */

class MushroomRepoTest {
    val mockDataSource = mockk<LocalMushroomDataSource>(relaxed = true)
    val repo = MushroomRepository(mockDataSource)

    // The repository should calculate the correct bounding box
    @Test
    fun calculatesCorrectBoundingBox() = runTest {
        val lat = 59.0
        val lon = 10.0
        val mushroomType = "Kantarell" // Does not give 0 in LocalSpeciesIdMapper

        val expectedResult = OccurrenceResult(0, emptyList())

        // Every call to getOccurrenceInBoundingBox() returns the expected result
        coEvery {
            mockDataSource.getOccurrencesInBoundingBox(
                any(),
                any(),
                any(),
                any(),
                any()
            ) } returns expectedResult

        // A call to getMushroomsNearLocation() should return the same result as the datasource
        val result = repo.getMushroomsNearLocation(
            lat = lat,
            lon = lon,
            mushroomType = mushroomType)

        // The result should match the expected one
        assertEquals(expectedResult, result)

        // Verifies that the bounding box is calculated correctly,
        // and that getOccurrencesInBoundingBox() is called exactly once.
        // Less and more tests that the result is in the correct range.
        coVerify(exactly = 1) {
            mockDataSource.getOccurrencesInBoundingBox(
                less(lat),
                more(lat),
                less(lon),
                more(lon),
                LocalSpeciesIdMapper.fromName(mushroomType)!!
            )
        }
    }
}

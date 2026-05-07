package no.uio.ifi.in2000.team16

import junit.framework.TestCase.assertEquals
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSource
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import org.junit.Test

/**
 * A test for SpeciesDataSource.
 * ----------------------------
 * A call to getAllMushrooms() must contain the right amount of mushrooms in the list.
 * "Kantarell"'s optimalAvgTemp must be set to 14.5 degrees
 * "Steinsopp"'s active months must be 6, 7, 8, 9, 10 and 11.
*/

class SpeciesDataSourceTest {

    @Test
    fun testSpecies() {
        val speciesDS = SpeciesDataSource()
        val listAllSpecies:  List<SpeciesProfile>  = speciesDS.allSpeciesProfiles
        speciesDS.getAllMushrooms() // Must be called in order to update amount variable

        assertEquals(listAllSpecies.count(),speciesDS.amount)
        //!! Says we guarantee the value returned won't be null, and if it is, crash the program
        assertEquals(listAllSpecies.find{it.name == "Kantarell"}!!.optimalAvgTemp, 14.5)
        assertEquals(listAllSpecies.find{it.name == "Steinsopp"}!!.activeMonths, listOf(6, 7, 8, 9, 10, 11),)
    }
}
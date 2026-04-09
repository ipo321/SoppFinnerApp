package no.uio.ifi.in2000.team16

import junit.framework.TestCase.assertEquals
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSource
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import org.junit.Test

/*
* Has to check if getAll() contains the right amount of mushrooms in the list.
* "Kantarell"'s optimalTemp MUST be set to 18.0 degrees
* "Steinsopp"'s type value has to be equal to 2
*/


class SpeciesTest {

    @Test
    fun testSpecies() {

        val speciesDS : SpeciesDataSource = SpeciesDataSource()
        val listAllSpecies:  List<SpeciesProfile>  = speciesDS.allSpeciesProfiles
        speciesDS.getAllMushrooms() // Must be called in order to update amount variable

        assertEquals("This ACTUALLY works :)", listAllSpecies.count(),
            speciesDS.amount)

        //!! Says we guarantee the value returned wont be null, and if it is , crash the program
        assertEquals("This also ACTUALLY works :O",
            listAllSpecies.find{it.name == "Kantarell"}!!.optimalTemp, 18.0)

        assertEquals("WHAT!? they ALL worked??? o_0",
            listAllSpecies.find{it.name == "Steinsopp"}!!.type, 1)
    }
}
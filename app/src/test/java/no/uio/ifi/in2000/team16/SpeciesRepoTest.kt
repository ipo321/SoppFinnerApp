package no.uio.ifi.in2000.team16

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.mockk.mockk
import no.uio.ifi.in2000.team16.data.species.SpeciesDataSource
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import org.junit.Assert.assertEquals
import org.junit.Test


//will return the spices by name
//Returns Kantarell (first object) object if the name is wrong
//It can get a list of Specie all spices


class SpeciesRepoTest{
    val speciesDS = SpeciesDataSource()
    val mockDataStore = mockk<DataStore<Preferences>>(relaxed = true)
    val repo = SpeciesRepository(speciesDS, mockDataStore)


    @Test
    fun testReturnWithKantarell(){
        val specie = repo.getMushroomByName("Kantarell")
        assertEquals("Kantarell", specie.commonName)
    }

    @Test
    fun testReturnWithSteinsopp(){
        val species = repo.getMushroomByName("Steinsopp")
        assertEquals("Steinsopp", species.commonName)
    }

    @Test
    fun wrongNamesearch() {
        val species = repo.getMushroomByName("wrong")
        assertEquals("Kantarell", species.commonName)
    }

    @Test
    fun testGetAllSpecies() {
        val allSpecies = repo.getAllMushrooms()
        assertEquals(3, allSpecies.size)
    }
}


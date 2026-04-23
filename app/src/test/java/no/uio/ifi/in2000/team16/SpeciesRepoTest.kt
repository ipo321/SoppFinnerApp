package no.uio.ifi.in2000.team16

import no.uio.ifi.in2000.team16.data.species.SpeciesRepository

import org.junit.Assert.assertEquals
import org.junit.Test




//will return the spices by name
//Returns Kantarell (first object) object if the name is wrong
//It can get a list of Specie all spices


class SpeciesRepoTest{
    val repo = SpeciesRepository()


    @Test
    fun TestRetrunswithKantarell(){
        val specie = repo.getSpeciesByName("Kantarell")
        assertEquals("Kantarell", specie.name)
    }

    @Test
    fun TestReturnWithSteinsopp(){
        val species = repo.getSpeciesByName("Steinsopp")
        assertEquals("Steinsopp", species.name)
    }

    @Test
    fun wrongNamesearch() {
        val species = repo.getSpeciesByName("wrong")
        assertEquals("Kantarell", species.name)
    }

    @Test
    fun testGetAllSpecies() {
        val allSpecies = repo.getAllSpecies()
        assertEquals(3, allSpecies.size)
    }

}


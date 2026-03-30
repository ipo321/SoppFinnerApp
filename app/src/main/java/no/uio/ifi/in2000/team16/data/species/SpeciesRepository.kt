package no.uio.ifi.in2000.team16.data.species

import javax.inject.Inject
import javax.inject.Singleton

//Repository That returns objects of MushroomInfo

@Singleton
class SpeciesRepository @Inject constructor(){
    val speciesDataSource = SpeciesDataSource()
    fun getSpeciesByName(name: String): SpeciesProfile{
        val allSpecies = speciesDataSource.getAllSpecies()
        return allSpecies.firstOrNull { it.name == name }
            ?: allSpecies.first()
    }
    fun getAllSpecies(): List<SpeciesProfile> {
        return speciesDataSource.getAllSpecies()
    }

    fun getMushroomInfoByName(name: String): SpeciesDataSource.MushroomInfo? {
        val allMushrooms = speciesDataSource.allMushrooms
        return allMushrooms.firstOrNull { it.commonName.equals(name, ignoreCase = true) }
            ?: allMushrooms.firstOrNull()
    }

    fun getAllMushrooms(): List<SpeciesDataSource.MushroomInfo> {
        return speciesDataSource.allMushrooms
    }

}


package no.uio.ifi.in2000.team16.data.species

import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import javax.inject.Inject
import javax.inject.Singleton

//Repository That returns objects of MushroomInfo

@Singleton
class SpeciesRepository @Inject constructor(
    private val speciesDataSource: SpeciesDataSource
){
    fun getMushroomByName(name: String): Mushroom {
        val all = speciesDataSource.getAllMushrooms()
        return all.firstOrNull { it.commonName.equals(name, ignoreCase = true) }
            ?: all.first()
    }

    fun getSpeciesProfileByName(name: String): SpeciesProfile {
        return getMushroomByName(name).profile
    }

    fun getAllMushrooms(): List<Mushroom> {
        return speciesDataSource.getAllMushrooms()
    }

    fun getAllSpeciesProfiles(): List<SpeciesProfile> {
        return speciesDataSource.getAllMushrooms().map { it.profile }
    }

    fun getMushroomCount(): Int {
        return speciesDataSource.getAmount()
    }

}


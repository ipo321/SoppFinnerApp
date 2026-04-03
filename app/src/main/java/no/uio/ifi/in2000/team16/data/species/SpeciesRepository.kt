package no.uio.ifi.in2000.team16.data.species

import no.uio.ifi.in2000.team16.domain.model.Mushroom
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

//Repository That returns objects of MushroomInfo

@Singleton
class SpeciesRepository @Inject constructor(
    private val speciesDataSource: SpeciesDataSource,
    private val dataStore: DataStore<Preferences>
){
    // Datastore keys
    private val SELECTED_MUSHROOM_KEY = stringPreferencesKey("selected_mushroom_name")
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_mushrooms_name")

    //StateFlows for UI
    private val _selectedMushroom = MutableStateFlow<Mushroom>(getDefaultMushroom())
    val selectedMushroom: StateFlow<Mushroom> = _selectedMushroom.asStateFlow()

    private val _favoriteMushrooms = MutableStateFlow<List<Mushroom>>(emptyList())
    val favoriteMushrooms: StateFlow<List<Mushroom>> = _favoriteMushrooms.asStateFlow()


    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadPersistedData()
        }
    }

    private fun getDefaultMushroom(): Mushroom {
        return getMushroomByName("Kantarell")

    }

    private suspend fun loadPersistedData() {
        //All saved settings
        val prefs = dataStore.data.first()

        // Selected mushroom
        val savedName = prefs[SELECTED_MUSHROOM_KEY]
        if (savedName != null) {
            _selectedMushroom.value = getMushroomByName(savedName)
        }

        // Favorite mushrooms
        val savedFavNames = prefs[FAVORITES_KEY]

        if (savedFavNames == null ) {
            val defaultFavorites = speciesDataSource.getAllMushrooms().take(3)
            _favoriteMushrooms.value = defaultFavorites

            val nameSet = mutableSetOf<String>()
            for (fav in defaultFavorites) nameSet.add(fav.commonName)
            dataStore.edit {it[FAVORITES_KEY] = nameSet }
        } else {
            val all = speciesDataSource.getAllMushrooms()
            val favoriteList = mutableListOf<Mushroom>()

            for (mushroom in all) {
                if (mushroom.commonName in savedFavNames) {
                    favoriteList.add(mushroom)
                }
            }
            _favoriteMushrooms.value = favoriteList
        }
    }

    suspend fun updateSelectedMushroom(mushroom: Mushroom) {
        _selectedMushroom.value = mushroom
        dataStore.edit {it[SELECTED_MUSHROOM_KEY] = mushroom.commonName}
    }

    suspend fun toggleFavorite(mushroom: Mushroom) {
        val currentFavorites = _favoriteMushrooms.value.toMutableList()

        //Check if its already in the list
        var exists = false
        for (fav in currentFavorites) {
            if (fav.commonName == mushroom.commonName) {
                exists = true
                break
            }
        }
        if (exists) {
            //Remove it if it is
            currentFavorites.remove(mushroom)
        } else {
            //Add it if not
            currentFavorites.add(mushroom)
        }
        //Update the stored values
        _favoriteMushrooms.value = currentFavorites

        val nameSet = mutableSetOf<String>()
        for (fav in currentFavorites) {
            nameSet.add(fav.commonName)
        }
        dataStore.edit {it[FAVORITES_KEY] = nameSet }

    }



    fun getMushroomByName(name: String): Mushroom {
        val all = speciesDataSource.getAllMushrooms()
        for (m in all) {
            if (m.commonName.equals(name, ignoreCase = true)) {
                return m
            }
        }
        return all[0] // Fallback
    }

    fun getAllMushrooms(): List<Mushroom> {
        return speciesDataSource.getAllMushrooms()
    }


    fun getMushroomCount(): Int {
        return speciesDataSource.getAmountofMushrooms()
    }

}


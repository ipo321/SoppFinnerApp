package no.uio.ifi.in2000.team16.data.species

import no.uio.ifi.in2000.team16.domain.model.Mushroom
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.*

// Repository That returns objects of MushroomInfo

/**
 * A domain-specific exception type for repository-level failures.
 */
class RepositoryException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

@Singleton
class SpeciesRepository @Inject constructor(
    private val speciesDataSource: SpeciesDataSource,
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val SELECTED_MUSHROOM_KEY =
            stringPreferencesKey("selected_mushroom_name")
        private val FAVORITES_KEY =
            stringSetPreferencesKey("favorite_mushrooms_name")

        private const val DEFAULT_SELECTED_NAME: String = "Kantarell"
        private const val DEFAULT_FAVORITES_COUNT: Int = 3
    }

    /**
     * Flow of the selected mushroom name (nullable if not set yet).
     */
    private val selectedMushroomNameFlow: Flow<String?> =
        dataStore.data
            .map { prefs -> prefs[SELECTED_MUSHROOM_KEY] }
            .distinctUntilChanged()

    /**
     * Public flow of the currently selected mushroom, backed by DataStore.
     *
     * - If no name is stored, falls back to DEFAULT_SELECTED_NAME.
     * - If that one is not found, falls back to the first available mushroom.
     * - If no mushrooms exist at all, throws an IllegalStateException.
     *
     * Uses the in-memory SpeciesDataSource synchronously (no suspend needed).
     */
    val selectedMushroom: Flow<Mushroom> =
        selectedMushroomNameFlow.map { storedName ->
            val all = try {
                speciesDataSource.getAllMushrooms()
            } catch (e: Exception) {
                throw RepositoryException(
                    message = "Failed to read all mushrooms while resolving selected mushroom",
                    cause = e
                )
            }

            if (all.isEmpty()) {
                throw IllegalStateException(
                    "No mushrooms available in SpeciesDataSource when resolving selected mushroom"
                )
            }

            val targetName = storedName ?: DEFAULT_SELECTED_NAME

            all.firstOrNull { it.commonName.equals(targetName, ignoreCase = true) }
                ?: all.first()
        }

    /**
     * Flow of favorite mushroom names (set of commonName).
     */
    private val favoriteNamesFlow: Flow<Set<String>> =
        dataStore.data
            .map { prefs -> prefs[FAVORITES_KEY] ?: emptySet() }
            .distinctUntilChanged()

    /**
     * Public flow of favorite mushrooms, resolved from the stored names.
     *
     * If a stored name cannot be found in the current species list, it is ignored.
     *
     * Uses the in-memory SpeciesDataSource synchronously.
     */
    val favoriteMushrooms: Flow<List<Mushroom>> =
        favoriteNamesFlow.map { savedNames ->
            val all = try {
                speciesDataSource.getAllMushrooms()
            } catch (e: Exception) {
                throw RepositoryException(
                    message = "Failed to read all mushrooms while resolving favorites",
                    cause = e
                )
            }

            if (all.isEmpty()) {
                emptyList()
            } else {
                all.filter { it.commonName in savedNames }
            }
        }

    /**
     * Call this once (e.g., from an Application initializer or a ViewModel)
     * to ensure sensible defaults are present in DataStore:
     * - A default selected mushroom (DEFAULT_SELECTED_NAME or the first available).
     * - Default favorites (first DEFAULT_FAVORITES_COUNT mushrooms).
     */
    suspend fun initializeDefaults() {
        try {
            dataStore.edit { prefs ->
                val all = try {
                    speciesDataSource.getAllMushrooms()
                } catch (e: Exception) {
                    throw RepositoryException(
                        message = "Failed to read all mushrooms while initializing defaults",
                        cause = e
                    )
                }

                if (all.isNotEmpty()) {
                    // Initialize selected mushroom if not already set.
                    if (prefs[SELECTED_MUSHROOM_KEY] == null) {
                        val defaultSelectedName =
                            all.firstOrNull {
                                it.commonName.equals(DEFAULT_SELECTED_NAME, ignoreCase = true)
                            }?.commonName
                                ?: all.first().commonName

                        prefs[SELECTED_MUSHROOM_KEY] = defaultSelectedName
                    }

                    // Initialize favorites if not already set.
                    if (prefs[FAVORITES_KEY] == null) {
                        val defaultFavorites = all.take(DEFAULT_FAVORITES_COUNT)
                        prefs[FAVORITES_KEY] =
                            defaultFavorites.map { it.commonName }.toSet()
                    }
                }
            }
        } catch (e: RepositoryException) {
            // Already wrapped with context; just rethrow.
            throw e
        } catch (e: Exception) {
            throw RepositoryException(
                message = "Failed to initialize default mushroom preferences",
                cause = e
            )
        }
    }

    /**
     * Update the selected mushroom in DataStore.
     * The public selectedMushroom flow will emit updated values.
     */
    suspend fun updateSelectedMushroom(mushroom: Mushroom) {
        try {
            dataStore.edit { prefs ->
                prefs[SELECTED_MUSHROOM_KEY] = mushroom.commonName
            }
        } catch (e: Exception) {
            throw RepositoryException(
                message = "Failed to update selected mushroom to '${mushroom.commonName}'",
                cause = e
            )
        }
    }

    /**
     * Toggle the favorite status of a given mushroom, by its commonName.
     *
     * We operate directly on the stored set of names; the favoriteMushrooms flow
     * will reflect the changes automatically.
     */
    suspend fun toggleFavorite(mushroom: Mushroom) {
        val name: String = mushroom.commonName

        try {
            dataStore.edit { prefs ->
                val current = prefs[FAVORITES_KEY] ?: emptySet()
                val mutable = current.toMutableSet()

                if (mutable.contains(name)) {
                    mutable.remove(name)
                } else {
                    mutable.add(name)
                }

                prefs[FAVORITES_KEY] = mutable
            }
        } catch (e: Exception) {
            throw RepositoryException(
                message = "Failed to toggle favorite status for mushroom '$name'",
                cause = e
            )
        }
    }

    /**
     * Lookup by name with safe fallback.
     * Returns null if there are no mushrooms at all.
     *
     * Non-suspend because SpeciesDataSource is in-memory.
     */
    fun getMushroomByName(name: String): Mushroom? {
        val all = try {
            speciesDataSource.getAllMushrooms()
        } catch (e: Exception) {
            throw RepositoryException(
                message = "Failed to read all mushrooms while looking up '$name'",
                cause = e
            )
        }

        if (all.isEmpty()) return null

        return all.firstOrNull { it.commonName.equals(name, ignoreCase = true) }
            ?: all.firstOrNull()
    }

    /**
     * Returns all mushrooms.
     *
     * Non-suspend because SpeciesDataSource is in-memory.
     */
    fun getAllMushrooms(): List<Mushroom> {
        return try {
            speciesDataSource.getAllMushrooms()
        } catch (e: Exception) {
            throw RepositoryException(
                message = "Failed to read all mushrooms",
                cause = e
            )
        }
    }

    /**
     * Returns the number of mushrooms.
     *
     * Non-suspend because SpeciesDataSource is in-memory.
     */
    fun getMushroomCount(): Int {
        return try {
            speciesDataSource.getAmountOfMushrooms()
        } catch (e: Exception) {
            throw RepositoryException(
                message = "Failed to read mushroom count",
                cause = e
            )
        }
    }
}

package no.uio.ifi.in2000.team16.data.artsdatabasen

import android.util.Log


/*
 * Maps app-internal mushroom names to their species ID in the GeoJSON dataset.
 * The IDs are assigned by this app, not by any external standard.
 */
object LocalSpeciesIdMapper {
    private const val TAG = "T16_LocalSpeciesIdMapper"

    /**
     * Returns the species ID for [name], or null if the name is not in the dataset.
     * A null result means no occurrence data exists for that mushroom — callers
     * should return an empty result rather than querying with an invalid ID.
     */
    fun fromName(name: String): Int? = when (name) {
        "Kantarell"           -> 1   // Cantharellus cibarius
        "Steinsopp"           -> 2   // Boletus edulis
        "Traktkantarell"      -> 3   // Craterellus tubaeformis
        "Blek piggsopp"       -> 4   // Hydnum repandum
        "Fåresopp"            -> 5   // Albatrellus ovinus
        "Granmatriske"        -> 6   // Lactarius deterrimus
        "Rødskrubb"           -> 7   // Leccinum versipelle
        "Brunskrubb"          -> 8   // Leccinum scabrum
        "Matblekksopp"        -> 9   // Coprinus comatus
        "Svart trompetsopp"   -> 10  // Craterellus cornucopioides
        "Skarlagens vårbeger" -> 11  // Sarcoscypha austriaca
        else -> {
            Log.w(TAG, "Unmapped mushroom name: '$name' — returning null (no occurrences will be found)")
            null
        }
    }
}

package no.uio.ifi.in2000.team16.data.artsdatabasen

object LocalSpeciesIdMapper {
        fun fromName(name: String): Int? = when (name) {
            "Kantarell" -> 1  // Cantharellus cibarius
            "Steinsopp" -> 2  // Boletus edulis
            "Skarlagen vårbeger" -> 11 // Sarcoscypha austriaca
            // add more when you add more mushrooms
            else -> null // treat as "all mushrooms" in local data
        }
    }

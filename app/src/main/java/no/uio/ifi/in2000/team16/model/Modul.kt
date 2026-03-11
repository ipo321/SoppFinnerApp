package no.uio.ifi.in2000.team16.model

import kotlinx.serialization.Serializable

/*
    Mock-klasse som per nå passer til alpakka-apiet sine stemmer.
 */
@Serializable
data class Modul (
    val id: String?
) {
    companion object {
    }
}

package no.uio.ifi.in2000.team16.data.kartverket

/*
    This interface makes it possible to search for addresses and places with the same functions
 */
interface SearchableLocation {
    val title: String
    val subtitle: String
    val latitude: Double?
    val longitude: Double?
}
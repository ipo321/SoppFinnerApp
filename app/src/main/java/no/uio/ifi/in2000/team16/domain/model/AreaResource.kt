package no.uio.ifi.in2000.team16.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AreaResource(
    val acres: String,
    val areaType: String,
    val woodSpecies: String,
    val forestQuality: String,
    val groundConditions: String,
    val verificationDate: String,
    val dataCaptureDate: String
)
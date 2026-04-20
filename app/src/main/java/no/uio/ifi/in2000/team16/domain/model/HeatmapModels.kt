package no.uio.ifi.in2000.team16.domain.model

//Data class for the area we want heatmap for
data class BoundingBox(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double
)

//a calculated point for the heatmap
data class HeatmapTile(
    val lat: Double,
    val lon: Double,
    val score: Int,
    val size: Double
)

data class GridPoint(
    val lat: Double,
    val lon: Double
)
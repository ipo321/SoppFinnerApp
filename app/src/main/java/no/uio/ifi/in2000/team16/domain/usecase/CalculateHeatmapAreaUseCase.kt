package no.uio.ifi.in2000.team16.domain.usecase

import android.util.Log
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team16.data.artsdatabasen.MushroomRepository
import no.uio.ifi.in2000.team16.data.met.frost.FrostRepository
import no.uio.ifi.in2000.team16.data.met.locationForecast.LocationForecastRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withTimeoutOrNull
import no.uio.ifi.in2000.team16.data.nibio.SatSkogRepository
import no.uio.ifi.in2000.team16.domain.logic.mushroom.ScorerProvider
import javax.inject.Inject
import no.uio.ifi.in2000.team16.domain.model.BoundingBox
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.GridPoint
import no.uio.ifi.in2000.team16.domain.model.HeatmapTile
import no.uio.ifi.in2000.team16.domain.model.SpeciesProfile
import java.util.Calendar
import kotlinx.coroutines.CancellationException
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.supervisorScope


class CalculateHeatmapAreaUseCase @Inject constructor(
    private val locationForecastRepository: LocationForecastRepository,
    private val mushroomRepository: MushroomRepository,
    private val forestRepository: SatSkogRepository,
    private val frostRepository: FrostRepository,
    private val scorerProvider: ScorerProvider,
){
    suspend operator fun invoke(
        bounds: BoundingBox,
        profile: SpeciesProfile,
        existingTiles: List<HeatmapTile> = emptyList()
    ): List<HeatmapTile> = coroutineScope {
        Log.d("T16_UC_Heatmap", "Starting heatmap calculation for ${profile.taxKey}")

        var finalTiles: List<HeatmapTile> = emptyList()

        val timeTaken = measureTimeMillis {
            try{
                finalTiles = supervisorScope {
                    val step = getStepSize(bounds.north - bounds.south)

                    //Make the grid to the resolution
                    val gridPoints = generateGridPoints(bounds, step)
                    val halfStep = step / 2.0
                    val existingTileMap = existingTiles
                        .filter { kotlin.math.abs(it.size - halfStep) < 0.0001 }
                        .associateBy { String.format(java.util.Locale.US, "%.5f_%.5f", it.lat, it.lon) }

                    val needsNewData = gridPoints.any { point ->
                        val key = String.format(java.util.Locale.US, "%.5f_%.5f", point.lat, point.lon)
                        existingTileMap[key] == null
                    }

                    //Find center
                    val centerLat = (bounds.north + bounds.south) / 2
                    val centerLon = (bounds.east + bounds.west) / 2

                    Log.d("T16_UC_Heatmap", "Fecthing data from center")

                    //Single calls
                    val weatherDeferred = if (needsNewData) {async { locationForecastRepository.weatherNext8Hours(centerLat, centerLon) }
                    } else null

                    val occurrencesDeferred = if (needsNewData) { async {
                        withTimeoutOrNull(5000L) {
                            mushroomRepository.getMushroomInBoundingBox(
                                north = bounds.north,
                                south = bounds.south,
                                east = bounds.east,
                                west = bounds.west,
                                mushRoomType = profile.name
                            )
                        }
                    }
                    } else null

                    val weather = weatherDeferred?.await()
                    val allOccurrencesInBox = occurrencesDeferred?.await()

                    val mushroomFindings = allOccurrencesInBox?.results?.size ?: 0
                    Log.d("T16_UC_Heatmap", "Found $mushroomFindings for ${profile.taxKey}")


                    //Starting with central ones
                    val sortedPoints = gridPoints.sortedBy { point ->
                        calculateDistance(centerLat, centerLon, point.lat, point.lon)
                    }

                    Log.d("T16_UC_Heatmap", "Generated ${gridPoints.size} grid points")

                    //Calculate score for each point and detailed calls
                    val heatmapJobs = sortedPoints.map { point ->
                        async(Dispatchers.Default) {
                            try {
                                val key = String.format(java.util.Locale.US, "%.5f_%.5f", point.lat, point.lon)
                                val cachedTile = existingTileMap[key]

                                if (cachedTile != null) {
                                    return@async HeatmapTile(point.lat, point.lon, cachedTile.score, step)
                                }

                                //Checking SAT-skog
                                val forestData =
                                    forestRepository.getForestDataForHeatmap(point.lat, point.lon)

                                if (forestData != null) {
                                    Log.d(
                                        "T16_UC_Heatmap",
                                        "Found forest, calling frost for: ${point.lat}, lon: ${point.lon}"
                                    )
                                    //Checking MET
                                    val weatherHistory =
                                        frostRepository.weatherHistory(point.lat, point.lon)

                                    //Checking Artsdatabanken
                                    val historicalFindsCount =
                                        allOccurrencesInBox?.results?.count { occ ->
                                            val occLat = occ.decimalLatitude ?: return@count false
                                            val occLon = occ.decimalLongitude ?: return@count false
                                            //Distance from point to occurrence
                                            calculateDistance(point.lat, point.lon, occLat, occLon) <= 500.0
                                        } ?: 0

                                    //Building the context
                                    val context = EnvironmentContext(
                                        currentWeather = weather,  //Only from the center of the map
                                        weatherHistory = weatherHistory,
                                        terrainData = null,
                                        ar5 = "Skog", // Since SAT-skog is not null
                                        forestData = forestData,
                                        historicalFindCount = historicalFindsCount,
                                        currentMonth = Calendar.getInstance()
                                            .get(Calendar.MONTH) + 1
                                    )

                                    //Run the evaluation
                                    val evaluation = scorerProvider.activeHeatScorer.calculateScore(context, profile)

                                    Log.d(
                                        "T16_UC_Heatmap",
                                        "Score ${evaluation.score} for point ${point.lat}"
                                    )

                                    //Makes the heatmap tile with a score
                                    HeatmapTile(point.lat, point.lon, evaluation.score, step)
                                } else {
                                    HeatmapTile(point.lat, point.lon, 0, step)
                                }
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                Log.e("T16_UC_Heatmap", "Error calculating heatmap tile: ${e.message}", e)
                                HeatmapTile(point.lat, point.lon, 0, step)
                            }
                        }
                    }

                    val originalTiles = heatmapJobs.awaitAll()

                    // Making fake points for higher resolution
                    val finalTilesList = mutableListOf<HeatmapTile>()

                    val tileMap = originalTiles.associateBy {
                        String.format(java.util.Locale.US, "%.5f_%.5f", it.lat, it.lon)
                    }
                    for (tile in originalTiles) {
                        finalTilesList.add(tile.copy(size = halfStep))

                        val rightKey = String.format(java.util.Locale.US, "%.5f_%.5f", tile.lat, tile.lon + step)
                        val topKey = String.format(java.util.Locale.US, "%.5f_%.5f", tile.lat + step, tile.lon)
                        val diagKey = String.format(java.util.Locale.US, "%.5f_%.5f", tile.lat + step, tile.lon + step)

                        val right = tileMap[rightKey]
                        val top = tileMap[topKey]
                        val diagonal = tileMap[diagKey]

                        if (right != null) {
                            finalTilesList.add(
                                HeatmapTile(
                                    lat = tile.lat,
                                    lon = tile.lon + halfStep,
                                    score = (tile.score + right.score) / 2,
                                    size = halfStep
                                )
                            )
                        }
                        if (top != null) {
                            finalTilesList.add(
                                HeatmapTile(
                                    lat = tile.lat + halfStep,
                                    lon = tile.lon,
                                    score = (tile.score + top.score) / 2,
                                    size = halfStep
                                )
                            )
                        }
                        if (right != null && top != null && diagonal != null) {
                            val avgScore = (tile.score + right.score + top.score + diagonal.score) / 4
                            finalTilesList.add(
                                HeatmapTile(
                                    lat = tile.lat + halfStep,
                                    lon = tile.lon + halfStep,
                                    score = avgScore,
                                    size = halfStep
                                )
                            )
                        }
                    }
                    finalTilesList
                }
            } catch (e: CancellationException) {
                Log.d("T16_UC_Heatmap", "Cancel, uses moved map")
                throw e
            } catch (e: Exception) {
                Log.e("T16_UC_Heatmap", "Error calculating heatmap: ${e.message}", e)
                finalTiles = emptyList()
            }
        }
        // Debug
        if (finalTiles.isEmpty()) {
            Log.w("T16_UC_Heatmap", "Finished in $timeTaken ms, but 0 tiles generated.")
        } else {
            Log.i("T16_UC_Heatmap", "Generated ${finalTiles.size} heatmap-tiles in $timeTaken ms.")
        }

        return@coroutineScope finalTiles
    }

    private fun getStepSize(mapHeight: Double): Double {
        return when {
            mapHeight > 1.2 -> 0.2      //130 km ->  22 km
            mapHeight > 0.6 -> 0.1      //66 km -> 11 km
            mapHeight > 0.24 -> 0.05     //26 km -> 5.5 km
            mapHeight > 0.12 -> 0.02     //13 km -> 2.2 km
            mapHeight > 0.06 -> 0.01     // 6.6 km -> 1.1 km
            mapHeight > 0.024 -> 0.005    //2.6 km ->  550 m
            mapHeight > 0.012 -> 0.002    //1.3 km -> 220 m
            else -> 0.001
        }
    }
    private fun generateGridPoints(bounds: BoundingBox, step: Double): List<GridPoint> {
        val points = mutableListOf<GridPoint>()

        val mapHeight = bounds.north - bounds.south

        val startLat = kotlin.math.floor(bounds.south / step) * step
        val endLat = kotlin.math.ceil(bounds.north / step) * step
        val startLon = kotlin.math.floor(bounds.west / step) * step
        val endLon = kotlin.math.ceil(bounds.east / step) * step

        var currentLat = startLat
        while (currentLat <= endLat + (step / 2)) {
            var currentLon = startLon
            while (currentLon <= endLon + (step / 2)) {
                val cleanLat = String.format(java.util.Locale.US, "%.5f", currentLat).toDouble()
                val cleanLon = String.format(java.util.Locale.US, "%.5f", currentLon).toDouble()

                points.add(GridPoint(cleanLat, cleanLon))
                currentLon += step
            }
            currentLat += step
        }
    return points
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2).let { it * it } +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) * kotlin.math.sin(dLon / 2).let { it * it }
        return r * 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    }
}

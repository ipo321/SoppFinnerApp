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
        resolution: Int = 7 //Numbers of row/columns in the grid (8 -> = 9x9 = 81 tiles)
    ): List<HeatmapTile> = coroutineScope {
        Log.d("T16_UC_Heatmap", "Starting heatmap calculation for ${profile.taxKey} Resolution: $resolution")

        var finalTiles: List<HeatmapTile> = emptyList()

        val timeTaken = measureTimeMillis {
            try{
                finalTiles = coroutineScope {
                    //Find center
                    val centerLat = (bounds.north + bounds.south) / 2
                    val centerLon = (bounds.east + bounds.west) / 2

                    Log.d("T16_UC_Heatmap", "Fecthing data from center")

                    //Single calls
                    val weatherDeferred = async { locationForecastRepository.weatherNext8Hours(centerLat, centerLon) }
                    val occurrencesDeferred = async {
                        withTimeoutOrNull(5000L){
                            mushroomRepository.getMushroomInBoundingBox(
                                north = bounds.north,
                                south = bounds.south,
                                east = bounds.east,
                                west = bounds.west,
                                mushRoomType = profile.taxKey
                            )
                        }
                    }

                    val weather = weatherDeferred.await()
                    val allOccurrencesInBox = occurrencesDeferred.await()

                    val mushroomFindings = allOccurrencesInBox?.results?.size ?: 0
                    Log.d("T16_UC_Heatmap", "Found $mushroomFindings for ${profile.taxKey}")

                    //Make the grid to the resolution
                    val rowRes = (resolution*1.5).toInt()
                    val gridPoints = generateGridPoints(bounds, rows = rowRes, cols = resolution)
                    Log.d("T16_UC_Heatmap", "Generated ${gridPoints.size} grid points")

                    //Calculate score for each point and detailed calls
                    val heatmapJobs = gridPoints.map { point ->
                        async {
                            try {
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
                                            calculateDistance(
                                                point.lat,
                                                point.lon,
                                                occLat,
                                                occLon
                                            ) <= 500.0

                                        } ?: 0

                                    //Building the context
                                    val context = EnvironmentContext(
                                        currentWeather = weather,  //Only from the center of the map
                                        weatherHistory = weatherHistory,
                                        terrainData = null,
                                        ar5 = "Skog", // Since SAT-skog is not null
                                        forestData = forestData,
                                        historicalFinds = historicalFindsCount,
                                        currentMonth = Calendar.getInstance()
                                            .get(Calendar.MONTH) + 1
                                    )


                                    //Run the evaluation
                                    val evaluation = scorerProvider.activeHeatScorer.calculateScore(
                                        context,
                                        profile
                                    )

                                    Log.d(
                                        "T16_UC_Heatmap",
                                        "Score ${evaluation.score} for point ${point.lat}"
                                    )

                                    //Makes the heatmap tile with a score
                                    HeatmapTile(point.lat, point.lon, evaluation.score)
                                } else {
                                    HeatmapTile(point.lat, point.lon, 0)
                                }
                            } catch (e: Exception) {
                                Log.e("T16_UC_Heatmap", "Error calculating heatmap tile: ${e.message}", e)
                                HeatmapTile(point.lat, point.lon, 0)
                            }
                        }
                    }
                    heatmapJobs.awaitAll()
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


    private fun generateGridPoints(bounds: BoundingBox, rows: Int, cols: Int): List<GridPoint> {
        val points = mutableListOf<GridPoint>()

        val latStep = (bounds.north - bounds.south) / rows
        val lonStep = (bounds.east - bounds.west) / cols

        for (i in 0..rows) {
            for (j in 0..cols) {
                points.add(
                    GridPoint(
                        lat = bounds.south + i * latStep,
                        lon = bounds.west + j * lonStep
                    )
                )
            }
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

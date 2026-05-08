package no.uio.ifi.in2000.team16

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorer
import no.uio.ifi.in2000.team16.domain.logic.mushroom.ScorerProvider
import no.uio.ifi.in2000.team16.domain.model.BoundingBox
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.HeatmapTile
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.domain.usecase.CalculateHeatmapAreaUseCase
import no.uio.ifi.in2000.team16.domain.usecase.GetEnvironmentContextUseCase
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.map.ScreenStatus
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
    A test for MapViewModel.
    ------------------------
    Requirements:
    - State is Idle when initiated
    - State is Loading when data is about to be fetched
    - State is Success when data is fetched
    - State is Error when data fetch fails
    - HeatMap is enabled when toggled, and tiles are fetched
 */

class MapViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val mockEnvironmentContext = mockk<GetEnvironmentContextUseCase>(relaxed = true)
    private val mockHeatMapArea = mockk<CalculateHeatmapAreaUseCase>(relaxed = true)
    private val mockScorerProvider = mockk<ScorerProvider>(relaxed = true)
    private val mockMushroomScorer = mockk<MushroomScorer>(relaxed = true)
    private lateinit var viewModel: MapViewModel
    private lateinit var fakeSpeciesRepository: FakeSpeciesRepository
    private lateinit var fakeLocationRepository: FakeLocationRepository

    private val lat = 59.0
    private val lon = 10.0

    // Setup happens before the test runs
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeSpeciesRepository = FakeSpeciesRepository()
        fakeLocationRepository = FakeLocationRepository()
        every { mockScorerProvider.activeScorer } returns mockMushroomScorer
        viewModel = MapViewModel(
            fakeSpeciesRepository,
            fakeLocationRepository,
            mockEnvironmentContext,
            mockHeatMapArea,
            mockScorerProvider
        )
    }

    // tearDown happens after the test runs
    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // State should be Idle when initiated
    @Test
    fun initialState() = runTest {
        val state = viewModel.uiState.value
        assertEquals(ScreenStatus.Idle, state.screenStatus)
    }

    // Should update state to Loading when data is about to be fetched
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadingState() = runTest {
        val mushroom = fakeSpeciesRepository.getAllMushrooms().first()
        // Makes sure the repository holds this mushroom
        fakeSpeciesRepository.selectedMushroom.value = mushroom
        advanceUntilIdle() // Makes sure the test waits until all coroutines are done

        coEvery { mockEnvironmentContext(any(), any(), any()) } coAnswers {
            delay(1000) // Simulates a slow network
            mockk(relaxed = true)
        }

        viewModel.onMapClicked(lat, lon) // Clicks map
        runCurrent() // Runs the viewmodel launch until delay()

        val state = viewModel.uiState.value
        // The state should be Loading when data is about to be fetched
        assertEquals(ScreenStatus.Loading, state.screenStatus)

        advanceUntilIdle() // Makes sure the test finishes all coroutines
    }

    // Should update state to Success when data is fetched
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun successState() = runTest {
        val mushroom = fakeSpeciesRepository.getAllMushrooms().first()
        val context = mockk<EnvironmentContext>(relaxed = true)
        val evaluation = EvaluationResult(85, "Bra!")

        fakeSpeciesRepository.selectedMushroom.value = mushroom
        advanceUntilIdle()

        // Makes sure the viewmodel uses the mock data, and does not try to connect to the internet
        coEvery { mockEnvironmentContext(any(), any(), any()) } returns context
        coEvery { mockMushroomScorer.calculateScore(any(), any()) } returns evaluation

        viewModel.onMapClicked(lat, lon) // Clicks map
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // The state should be Success after fetching data
        assertEquals(ScreenStatus.Success, state.screenStatus)
        // The state's evaluation should be the same as the one calculated
        assertEquals(evaluation, state.evaluation)
        // The state's context should be the same as the one fetched
        assertEquals(context, state.currentContext)
    }

    // Should update state to Error when data fetch fails
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun errorState() = runTest {
        // Makes sure the viewmodel throws exception when trying to fetch context
        coEvery { mockEnvironmentContext(any(), any(), any()) } throws Exception("Test error")

        viewModel.onMapClicked(lat, lon) // Clicks map
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // The state should be Error when fetching context fails
        assertEquals(ScreenStatus.Error("Test error"), state.screenStatus)
    }

    // HeatMap should be enabled when toggled, and tiles should be fetched
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fetchHeatMapSuccessState() = runTest {
        val bounds = BoundingBox(lat, 58.0, 11.0, lon)
        val mushroom = mockk<Mushroom>(relaxed = true)
        val tiles = listOf(HeatmapTile(lat, lon, 80, 0.032358))

        fakeSpeciesRepository.selectedMushroom.value = mushroom
        advanceUntilIdle()

        viewModel.toggleHeatmap()
        coEvery { mockHeatMapArea(any(), any()) } returns tiles
        viewModel.fetchHeatmapData(bounds)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // The state's tiles should be the same as the ones fetched
        assertEquals(tiles, state.heatmapTiles)
        // The state's isFetchingHeatmap should be false after tiles are fetched
        assertFalse(state.isFetchingHeatmap)
        // The state's isHeatmapEnabled should be true after toggling heatmap
        assertTrue(state.isHeatmapEnabled)
    }
}
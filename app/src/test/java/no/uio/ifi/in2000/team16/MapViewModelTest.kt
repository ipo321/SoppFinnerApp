package no.uio.ifi.in2000.team16

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.logic.mushroom.MushroomScorer
import no.uio.ifi.in2000.team16.domain.logic.mushroom.ScorerProvider
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.model.EvaluationResult
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.domain.usecase.CalculateHeatmapAreaUseCase
import no.uio.ifi.in2000.team16.domain.usecase.GetEnvironmentContextUseCase
import no.uio.ifi.in2000.team16.ui.map.MapViewModel
import no.uio.ifi.in2000.team16.ui.map.ScreenStatus
import org.junit.After
import org.junit.Before
import org.junit.Test

class MapViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<SpeciesRepository>(relaxed = true)
    private val mockEnvironmentContext = mockk<GetEnvironmentContextUseCase>(relaxed = true)
    private val mockHeatMapArea = mockk<CalculateHeatmapAreaUseCase>(relaxed = true)
    private val mockScorerProvider = mockk<ScorerProvider>(relaxed = true)
    private val mockMushroomScorer = mockk<MushroomScorer>(relaxed = true)
    private lateinit var viewModel: MapViewModel

    // MutableStateFlow objects that the test controls
    private val selectedMushroomFlow = MutableStateFlow<Mushroom>(mockk(relaxed = true))
    private val favoriteMushroomsFlow = MutableStateFlow<List<Mushroom>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockScorerProvider.activeScorer } returns mockMushroomScorer
        every { mockRepository.favoriteMushrooms } returns favoriteMushroomsFlow
        every { mockRepository.selectedMushroom } returns selectedMushroomFlow
        viewModel = MapViewModel(
            mockRepository,
            mockEnvironmentContext,
            mockHeatMapArea,
            mockScorerProvider
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Should update state to Success when data is fetched
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun onMapClicked() = runTest {
        val lat = 59.0
        val lon = 10.0
        val mushroom = mockk<Mushroom>(relaxed = true)
        val context = mockk<EnvironmentContext>(relaxed = true)
        val evaluation = EvaluationResult(85, "Bra!")

        // Makes sure the viewmodel selects this mushroom
        selectedMushroomFlow.value = mushroom
        advanceUntilIdle()

        // Makes sure the viewmodel uses the mock data, and does not try to connect to the internet
        coEvery { mockEnvironmentContext(any(), any(), any()) } returns context
        coEvery { mockMushroomScorer.calculateScore(any(), any()) } returns evaluation

        viewModel.onMapClicked(lat, lon) // Clicks map
        advanceUntilIdle() // Makes sure the test wait until idle

        val state = viewModel.uiState.value
        // The state should be Success after fetching data
        assert(state.screenStatus is ScreenStatus.Success)
        // The evaluation should be the same as the one calculated
        assertEquals(evaluation, state.evaluation)
        // The context should be the same as the one fetched
        assertEquals(context, state.currentContext)
    }
}
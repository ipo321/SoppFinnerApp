package no.uio.ifi.in2000.team16

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
    A test for MushroomInfoViewModel
    ---------------------------------
    The test checks if the viewmodel receives a mushroom

    Requirements:
    Hold the chosen mushroom
    Receive a mushroom when clicking on one
    Update mushroom choice
*/

class MushroomInfoViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun selectMushroom() = runTest {
        val fakeRepository = FakeSpeciesRepository()
        val viewModel = MushroomInfoViewModel(fakeRepository)

        advanceUntilIdle() // Lets the init finish before moving on

        var mushroom = fakeRepository.getAllMushrooms()[1]
        viewModel.onMushroomSelected(mushroom)

        advanceUntilIdle() // Lets the launch finish before moving on

        var selectedUi = viewModel.uiState.value.selectedMushroom

        // Initial state: should hold the chosen mushroom
        assertNotNull(selectedUi)
        assertEquals(mushroom, selectedUi)

        // Should update mushroom choice
        mushroom = fakeRepository.getAllMushrooms()[0]
        viewModel.onMushroomSelected(mushroom)

        advanceUntilIdle()

        selectedUi = viewModel.uiState.value.selectedMushroom
        assertEquals(mushroom, selectedUi)

        // Should update the chosen mushroom for repository
        val selectedRepo = fakeRepository.selectedMushroom.value
        assertEquals(mushroom, selectedRepo)
    }
}
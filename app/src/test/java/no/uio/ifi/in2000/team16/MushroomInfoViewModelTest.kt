package no.uio.ifi.in2000.team16

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel
import org.junit.Test


class MushroomInfoViewModelTest () {
    /*
   A test for MushroomInfoViewModel
   ---------------------------------
   The test checks if the viewmodel receives a mushroom

   Requirements:
   Hold the chosen mushroom
   Receive a mushroom when clicking on one
   Update mushroom choice
*/

    @Test
    fun selectMushroom() {
        val mockRepository = mockk<SpeciesRepository>(relaxed = true)
        val mockMushrooms = listOf(
            mockk<Mushroom>(relaxed = true),
            mockk<Mushroom>(relaxed = true)
        )
        every { mockRepository.getAllMushrooms() } returns mockMushrooms
        val viewModel = MushroomInfoViewModel(mockRepository)
        // Initial state: should hold the chosen mushroom
        assertNotNull(viewModel.selectedMushroom)
        assertEquals(mockMushrooms[0], viewModel.selectedMushroom)
        // Should update mushroom choice
        viewModel.selectMushroom(mockMushrooms[1])
        assertEquals(mockMushrooms[1], viewModel.selectedMushroom)
        // assertEquals is better practice than assertNotEquals
    }
}
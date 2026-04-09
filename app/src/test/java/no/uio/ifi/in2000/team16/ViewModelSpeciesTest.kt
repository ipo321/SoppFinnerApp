package no.uio.ifi.in2000.team16


import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import no.uio.ifi.in2000.team16.data.species.SpeciesRepository
import no.uio.ifi.in2000.team16.domain.model.Mushroom
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel
import org.junit.Assert.assertNotEquals
import org.junit.Test


class ViewModelSpeciesTest () {


    /*
   En test for MushroomInfoViewModel
   ---------------------------------
   Testen sjekker om viewmodel mottar en sopp

   Krav:
   Holde på den valgte soppen
   Motta en sopp man trykker på
   Oppdater soppvalget

     */

    @Test
    fun getMushroom() {
        val mockRepository = mockk<SpeciesRepository>(relaxed = true)
        val mockMushrooms = listOf(
            mockk<Mushroom>(relaxed = true),
            mockk<Mushroom>(relaxed = true)
        )
        every { mockRepository.getAllMushrooms() } returns mockMushrooms
        val viewModel = MushroomInfoViewModel(mockRepository)
        val newMushroom = viewModel.mushrooms[1]
        assertNotNull(viewModel.selectedMushroom)
        assertNotEquals(newMushroom, viewModel.selectedMushroom)
    }
}
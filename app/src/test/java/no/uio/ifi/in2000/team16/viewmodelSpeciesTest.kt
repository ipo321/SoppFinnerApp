package no.uio.ifi.in2000.team16


import androidx.lifecycle.viewmodel.compose.viewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.selects.select
import no.uio.ifi.in2000.team16.domain.model.allMushrooms
import no.uio.ifi.in2000.team16.ui.mushroomInfo.MushroomInfoViewModel
import org.junit.Assert.assertNotEquals
import org.junit.Test


class ViewmodelSpeciesTest (
    ){


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
    fun GetMushroom() {
        val viewModel = MushroomInfoViewModel()
        val newMushroom = allMushrooms[1]
        assertNotNull(viewModel.selectedMushroom)

        assertNotEquals(newMushroom, viewModel.selectedMushroom)
,
    }
}
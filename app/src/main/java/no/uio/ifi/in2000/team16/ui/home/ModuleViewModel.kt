package no.uio.ifi.in2000.team16.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.domain.model.EnvironmentContext
import no.uio.ifi.in2000.team16.domain.usecase.GetEnvironmentContextUseCase
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val getEnvironmentContextUseCase: GetEnvironmentContextUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ModulUiState>>(UiState.Idle)
    val uiState : StateFlow<UiState<ModulUiState>> = _uiState.asStateFlow()

    fun testEnvironmentUseCase() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                Log.d("API_TEST", "Henter alt av vær og skog parallelt...")

                val lat = 60.008
                val lon = 10.686

                val contextResult = getEnvironmentContextUseCase(lat, lon, null)

                Log.d("API_TEST", "SUKSESS! Hentet EnvironmentContext: $contextResult")
                _uiState.value = UiState.Success(ModulUiState(contextResult))

            } catch (e: Exception) {
                Log.e("API_TEST", "Krasj i UseCase: ${e.message}")
                _uiState.value = UiState.Error
            }
        }
    }
}

data class ModulUiState(val environmentContext: EnvironmentContext)
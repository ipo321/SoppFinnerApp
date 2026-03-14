package no.uio.ifi.in2000.team16.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.modul.ModulDataSource
import no.uio.ifi.in2000.team16.domain.model.Modul
import javax.inject.Inject

/*
    Implementerer HiltViewModel fordi dette er en ViewModel som gjør det enkelt med
    Dependency Injection.

    Per nå en default for et ViewModel.
 */

@HiltViewModel
class ModulViewModel @Inject constructor(
    private val kartDataSource: ModulDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ModulUiState>>(UiState.Idle)
    val uiState : StateFlow<UiState<ModulUiState>> = _uiState.asStateFlow()

    init {
        hentModul()
    }

    fun hentModul() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val modul = kartDataSource.hentModul()
                _uiState.value = UiState.Success(ModulUiState(modul))
            } catch (e: Exception) {
                _uiState.value = UiState.Error
            }
        }
    }
}

data class ModulUiState(val modul: List<Modul>)

sealed class UiState <out T> {
    object Idle : UiState<Nothing>()
    object Error : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
}
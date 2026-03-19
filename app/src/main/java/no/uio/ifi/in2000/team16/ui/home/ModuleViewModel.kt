package no.uio.ifi.in2000.team16.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.example.ModuleDataSource
import no.uio.ifi.in2000.team16.domain.model.ExampleClass
import javax.inject.Inject

/*
    Implementerer HiltViewModel fordi dette er en ViewModel som gjør det enkelt med
    Dependency Injection.

    Per nå en default for et ViewModel.
 */

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val kartDataSource: ModuleDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<ModulUiState>>(UiState.Idle)
    val uiState : StateFlow<UiState<ModulUiState>> = _uiState.asStateFlow()

    init {
        getModule()
    }

    fun getModule() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val module = kartDataSource.getModule()
                _uiState.value = UiState.Success(ModulUiState(module))
            } catch (e: Exception) {
                _uiState.value = UiState.Error
            }
        }
    }
}

data class ModulUiState(val exampleClass: List<ExampleClass>)


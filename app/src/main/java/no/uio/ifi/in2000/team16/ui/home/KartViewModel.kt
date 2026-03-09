package no.uio.ifi.in2000.team16.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.kart.KartDataSource
import no.uio.ifi.in2000.team16.model.Kart
import javax.inject.Inject

@HiltViewModel
class KartViewModel @Inject constructor(
    private val kartDataSource: KartDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<KartUiState>>(UiState.Idle)
    val uiState : StateFlow<UiState<KartUiState>> = _uiState.asStateFlow()

    init {
        hentKart()
    }

    fun hentKart() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val kart = kartDataSource.hentKart()
                _uiState.value = UiState.Success(KartUiState(kart))
            } catch (e: Exception) {
                _uiState.value = UiState.Error
            }
        }
    }
}

data class KartUiState(val kart: List<Kart>)

sealed class UiState <out T> {
    object Idle : UiState<Nothing>()
    object Error : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
}
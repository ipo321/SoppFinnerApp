package no.uio.ifi.in2000.team16.ui.common

sealed class UiState <out T> {
    object Idle : UiState<Nothing>()
    object Error : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
}
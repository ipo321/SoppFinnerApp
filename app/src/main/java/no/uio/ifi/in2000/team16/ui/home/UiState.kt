package no.uio.ifi.in2000.team16.ui.home

/*
    General class for UiState, because a sealed class UiState can only be declared once
*/

sealed class UiState <out T> {
    object Idle : UiState<Nothing>()
    object Error : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
}
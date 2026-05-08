package no.uio.ifi.in2000.team16.ui.loading

import no.uio.ifi.in2000.team16.ui.navigation.Screen
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team16.data.onBoard.DataStoreRepositoryInterface
import javax.inject.Inject


/*
 This ViewModel controls the splash/loading screen.
 It checks if onboarding has been completed and decides
 which screen the app should open first.
*/
@Suppress("unused")
@HiltViewModel
class SplashViewModel @Inject constructor(
    // Repository used to read the saved onboarding state
    private val repository: DataStoreRepositoryInterface
) : ViewModel() {
    // Private loading state that can only be changed inside the ViewModel
    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<String> = mutableStateOf(Screen.OnboardScreen.route)
    val startDestination: State<String> = _startDestination

    init {
        viewModelScope.launch {
            repository.readOnBoardingState().collect { completed ->
                if (completed) {
                    _startDestination.value = Screen.Map.route
                } else {
                    _startDestination.value = Screen.OnboardScreen.route
                }
            }
            _isLoading.value = false
        }
    }

}
package com.firkat.intervaltraining.feature.loadworkout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firkat.intervaltraining.R
import com.firkat.intervaltraining.core.resources.StringProvider
import com.firkat.intervaltraining.domain.usecase.GetWorkoutByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoadWorkoutViewModel @Inject constructor(
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    private val stringProvider: StringProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoadWorkoutUiState())
    val uiState: StateFlow<LoadWorkoutUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoadWorkoutEvent>()
    val events: SharedFlow<LoadWorkoutEvent> = _events.asSharedFlow()

    fun onAction(action: LoadWorkoutAction) {
        when (action) {
            is LoadWorkoutAction.WorkoutIdChanged -> {
                _uiState.update { it.copy(workoutIdInput = action.value) }
            }

            LoadWorkoutAction.SubmitClicked -> loadWorkout()
        }
    }

    private fun loadWorkout() {
        val workoutId = uiState.value.workoutIdInput.trim()
        if (workoutId.isBlank()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                getWorkoutByIdUseCase(workoutId)
            }.onSuccess { workout ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        lastLoadedWorkoutTitle = workout.title,
                    )
                }
                _events.emit(LoadWorkoutEvent.NavigateToTraining(workoutId))
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = stringProvider.getString(R.string.error_load_workout_failed),
                    )
                }
            }
        }
    }
}

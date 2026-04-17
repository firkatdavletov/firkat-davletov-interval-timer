package com.firkat.intervaltraining.feature.loadworkout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firkat.intervaltraining.domain.usecase.GetWorkoutByIdUseCase
import com.firkat.intervaltraining.domain.usecase.ObserveLastWorkoutIdUseCase
import com.firkat.intervaltraining.domain.usecase.SaveLastWorkoutIdUseCase
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
    private val saveLastWorkoutIdUseCase: SaveLastWorkoutIdUseCase,
    observeLastWorkoutIdUseCase: ObserveLastWorkoutIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoadWorkoutUiState())
    val uiState: StateFlow<LoadWorkoutUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoadWorkoutEvent>()
    val events: SharedFlow<LoadWorkoutEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            observeLastWorkoutIdUseCase().collect { lastWorkoutId ->
                if (!lastWorkoutId.isNullOrBlank() && _uiState.value.workoutIdInput.isBlank()) {
                    _uiState.update { current ->
                        current.copy(workoutIdInput = lastWorkoutId)
                    }
                }
            }
        }
    }

    fun onAction(action: LoadWorkoutAction) {
        when (action) {
            is LoadWorkoutAction.WorkoutIdChanged -> {
                _uiState.update { it.copy(workoutIdInput = action.value) }
            }

            LoadWorkoutAction.SubmitClicked -> loadWorkout()
            LoadWorkoutAction.ClearErrorClicked -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadWorkout() {
        val workoutId = uiState.value.workoutIdInput.trim()
        if (workoutId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите id тренировки") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                getWorkoutByIdUseCase(workoutId)
            }.onSuccess { workout ->
                saveLastWorkoutIdUseCase(workoutId)
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
                        errorMessage = "Не удалось загрузить тренировку",
                    )
                }
            }
        }
    }
}

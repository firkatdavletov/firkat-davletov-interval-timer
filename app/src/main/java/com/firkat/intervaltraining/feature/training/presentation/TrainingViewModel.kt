package com.firkat.intervaltraining.feature.training.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firkat.intervaltraining.domain.usecase.GetWorkoutByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class TrainingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null

    init {
        val workoutId = savedStateHandle.get<String>(WORKOUT_ID_ARG).orEmpty()
        if (workoutId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Workout id не найден",
                )
            }
        } else {
            loadWorkout(workoutId)
        }
    }

    fun onAction(action: TrainingAction) {
        when (action) {
            TrainingAction.StartPauseClicked -> toggleRunning()
            TrainingAction.ResetClicked -> resetWorkout()
            TrainingAction.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadWorkout(workoutId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    workoutId = workoutId,
                    isLoading = true,
                    errorMessage = null,
                )
            }

            runCatching { getWorkoutByIdUseCase(workoutId) }
                .onSuccess { workout ->
                    _uiState.update {
                        it.copy(
                            workoutTitle = workout.title,
                            segments = workout.intervals,
                            currentSegmentIndex = 0,
                            elapsedSeconds = workout.intervals.firstOrNull()?.totalSeconds ?: 0,
                            isRunning = false,
                            isLoading = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Не удалось загрузить тренировку",
                        )
                    }
                }
        }
    }

    private fun toggleRunning() {
        val canRun = _uiState.value.segments.isNotEmpty()
        if (!canRun) return

        val shouldRun = !_uiState.value.isRunning
        _uiState.update { it.copy(isRunning = shouldRun) }

        if (shouldRun) {
            startTicker()
        } else {
            tickerJob?.cancel()
            tickerJob = null
        }
    }

    private fun startTicker() {
        if (tickerJob?.isActive == true) return

        tickerJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000L)
                tick()
            }
        }
    }

    private fun tick() {
        val current = _uiState.value
        if (!current.isRunning || current.segments.isEmpty()) return

        if (current.elapsedSeconds > 1) {
            _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds - 1) }
            return
        }

        val nextIndex = current.currentSegmentIndex + 1
        if (nextIndex >= current.segments.size) {
            _uiState.update {
                it.copy(
                    isRunning = false,
                    currentSegmentIndex = current.segments.lastIndex,
                    elapsedSeconds = 0,
                )
            }
            tickerJob?.cancel()
            tickerJob = null
            return
        }

        _uiState.update {
            it.copy(
                currentSegmentIndex = nextIndex,
                elapsedSeconds = current.segments[nextIndex].totalSeconds,
            )
        }
    }

    private fun resetWorkout() {
        val firstDuration = _uiState.value.segments.firstOrNull()?.totalSeconds ?: 0
        tickerJob?.cancel()
        tickerJob = null
        _uiState.update {
            it.copy(
                currentSegmentIndex = 0,
                elapsedSeconds = firstDuration,
                isRunning = false,
            )
        }
    }

    companion object {
        const val WORKOUT_ID_ARG = "workoutId"
    }
}

package com.firkat.intervaltraining.feature.training.presentation

import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.ui.model.IntervalTimerState
import com.firkat.intervaltraining.ui.model.WorkoutTimerState

data class TrainingUiState(
    val workoutId: String = "",
    val workoutTitle: String = "",
    val segments: List<IntervalSegment> = emptyList(),
    val workoutTimerState: WorkoutTimerState = WorkoutTimerState.Pending,
    val timerState: IntervalTimerState = IntervalTimerState.Pending,
    val currentSegmentIndex: Int = 0,
    val elapsedSeconds: Int = 0,
    val workoutTotalSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

sealed interface TrainingAction {
    data object StartPauseClicked : TrainingAction
    data object ResetClicked : TrainingAction
    data object DismissError : TrainingAction
}

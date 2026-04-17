package com.firkat.intervaltraining.feature.training.presentation

import com.firkat.intervaltraining.core.model.IntervalSegment

data class TrainingUiState(
    val workoutId: String = "",
    val workoutTitle: String = "",
    val segments: List<IntervalSegment> = emptyList(),
    val currentSegmentIndex: Int = 0,
    val secondsLeftInSegment: Int = 0,
    val isRunning: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

sealed interface TrainingAction {
    data object StartPauseClicked : TrainingAction
    data object ResetClicked : TrainingAction
    data object DismissError : TrainingAction
}

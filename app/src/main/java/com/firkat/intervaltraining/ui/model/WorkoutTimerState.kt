package com.firkat.intervaltraining.ui.model

sealed interface WorkoutTimerState {
    data object Pending : WorkoutTimerState

    data object Started : WorkoutTimerState

    data object Paused : WorkoutTimerState

    data object Completed : WorkoutTimerState
}

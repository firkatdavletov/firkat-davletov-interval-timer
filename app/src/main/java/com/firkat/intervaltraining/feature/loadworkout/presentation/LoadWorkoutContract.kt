package com.firkat.intervaltraining.feature.loadworkout.presentation

data class LoadWorkoutUiState(
    val workoutIdInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastLoadedWorkoutTitle: String? = null,
)

sealed interface LoadWorkoutAction {
    data class WorkoutIdChanged(val value: String) : LoadWorkoutAction
    data object SubmitClicked : LoadWorkoutAction
    data object ClearErrorClicked : LoadWorkoutAction
}

sealed interface LoadWorkoutEvent {
    data class NavigateToTraining(val workoutId: String) : LoadWorkoutEvent
}

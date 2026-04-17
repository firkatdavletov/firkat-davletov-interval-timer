package com.firkat.intervaltraining.app.navigation

import android.net.Uri

sealed class AppDestination(val route: String) {
    data object LoadWorkout : AppDestination("load_workout")
    data object Training : AppDestination("training/{workoutId}") {
        fun createRoute(workoutId: String): String = "training/${Uri.encode(workoutId)}"
    }
}

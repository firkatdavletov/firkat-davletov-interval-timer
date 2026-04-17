package com.firkat.intervaltraining.core.data.datastore

import kotlinx.coroutines.flow.Flow

interface WorkoutPrefsDataStore {
    val lastWorkoutId: Flow<String?>

    suspend fun saveLastWorkoutId(workoutId: String)
}

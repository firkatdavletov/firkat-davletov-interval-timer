package com.firkat.intervaltraining.domain.repository

import com.firkat.intervaltraining.core.model.Workout

interface WorkoutRepository {
    suspend fun getWorkoutById(id: String): Workout
}

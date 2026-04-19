package com.firkat.intervaltraining.domain.repository

import com.firkat.intervaltraining.core.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    suspend fun getWorkoutById(id: String): Workout
}

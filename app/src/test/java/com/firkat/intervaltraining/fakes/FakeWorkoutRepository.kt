package com.firkat.intervaltraining.fakes

import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWorkoutRepository(
    private val workouts: MutableMap<String, Workout> = mutableMapOf(),
) : WorkoutRepository {

    private val lastWorkoutIdFlow = MutableStateFlow<String?>(null)

    var shouldThrow = false

    override suspend fun getWorkoutById(id: String): Workout {
        if (shouldThrow) error("Network error")
        return workouts[id] ?: error("Workout not found")
    }

    override fun observeLastWorkoutId(): Flow<String?> = lastWorkoutIdFlow

    override suspend fun saveLastWorkoutId(workoutId: String) {
        lastWorkoutIdFlow.value = workoutId
    }
}

package com.firkat.intervaltraining.fakes

import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.repository.WorkoutRepository

class FakeWorkoutRepository(
    private val workouts: MutableMap<String, Workout> = mutableMapOf(),
) : WorkoutRepository {

    var shouldThrow = false

    override suspend fun getWorkoutById(id: String): Workout {
        if (shouldThrow) error("Network error")
        val workout = workouts[id] ?: error("Workout not found")
        return workout
    }
}

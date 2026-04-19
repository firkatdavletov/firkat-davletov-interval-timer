package com.firkat.intervaltraining.domain.usecase

import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.repository.WorkoutRepository
import javax.inject.Inject

class GetWorkoutByIdUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(param: String): Workout {
        return workoutRepository.getWorkoutById(param)
    }
}

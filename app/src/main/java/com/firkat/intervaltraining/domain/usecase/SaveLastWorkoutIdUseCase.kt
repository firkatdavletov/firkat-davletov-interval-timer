package com.firkat.intervaltraining.domain.usecase

import com.firkat.intervaltraining.domain.repository.WorkoutRepository
import javax.inject.Inject

class SaveLastWorkoutIdUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(workoutId: String) = workoutRepository.saveLastWorkoutId(workoutId)
}

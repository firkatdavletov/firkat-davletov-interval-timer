package com.firkat.intervaltraining.domain.usecase

import com.firkat.intervaltraining.domain.repository.WorkoutRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveLastWorkoutIdUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    operator fun invoke(): Flow<String?> = workoutRepository.observeLastWorkoutId()
}

package com.firkat.intervaltraining.core.data.repository

import com.firkat.intervaltraining.BuildConfig
import com.firkat.intervaltraining.core.data.remote.api.WorkoutApi
import com.firkat.intervaltraining.core.data.remote.dto.toDomain
import com.firkat.intervaltraining.core.di.IoDispatcher
import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.repository.WorkoutRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutApi: WorkoutApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : WorkoutRepository {

    override suspend fun getWorkoutById(id: String): Workout = withContext(ioDispatcher) {
        if (BuildConfig.MOCK_ENABLED) {
            delay(1_000)
            return@withContext mockWorkout(id)
        }

        workoutApi.getWorkoutById(id).toDomain()
    }

    private fun mockWorkout(id: String): Workout = Workout(
        id = id,
        title = "Mock Interval Training",
        totalTime = 195,
        elapsedTime = 0,
        intervals = listOf(
            IntervalSegment(name = "Разминка", totalSeconds = 60, elapsedSeconds = 0),
            IntervalSegment(name = "Быстрый бег", totalSeconds = 45, elapsedSeconds = 0),
            IntervalSegment(name = "Ходьба", totalSeconds = 30, elapsedSeconds = 0),
            IntervalSegment(name = "Прыжки", totalSeconds = 40, elapsedSeconds = 0),
            IntervalSegment(name = "Отдых", totalSeconds = 20, elapsedSeconds = 0),
        ),
    )
}

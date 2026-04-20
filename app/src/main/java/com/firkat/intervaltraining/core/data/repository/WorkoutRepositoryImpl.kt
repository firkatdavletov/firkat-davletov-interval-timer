package com.firkat.intervaltraining.core.data.repository

import com.firkat.intervaltraining.BuildConfig
import com.firkat.intervaltraining.core.data.remote.api.WorkoutApi
import com.firkat.intervaltraining.core.data.remote.dto.toDomain
import com.firkat.intervaltraining.core.di.IoDispatcher
import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.repository.WorkoutRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

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
        title = "Тренировка 7",
        totalTime = 30,
        elapsedTime = 0,
        intervals = listOf(
            IntervalSegment(name = "Ходьба в среднем темпе", totalSeconds = 10, elapsedSeconds = 0),
            IntervalSegment(name = "Ходьба в интенсивном темпе", totalSeconds = 10, elapsedSeconds = 0),
            IntervalSegment(name = "Ходьба в среднем темпе", totalSeconds = 10, elapsedSeconds = 0),
//            IntervalSegment(name = "Медленный бег", totalSeconds = 30, elapsedSeconds = 0),
//            IntervalSegment(name = "Ходьба в среднем темпе", totalSeconds = 90, elapsedSeconds = 0),
//            IntervalSegment(name = "Медленный бег", totalSeconds = 30, elapsedSeconds = 0),
//            IntervalSegment(name = "Ходьба в среднем темпе", totalSeconds = 30, elapsedSeconds = 0),
        ),
    )
}

package com.firkat.intervaltraining.core.data.repository

import com.firkat.intervaltraining.core.data.datastore.WorkoutPrefsDataStore
import com.firkat.intervaltraining.core.data.remote.api.WorkoutApi
import com.firkat.intervaltraining.core.data.remote.dto.toDomain
import com.firkat.intervaltraining.core.di.IoDispatcher
import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.repository.WorkoutRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutApi: WorkoutApi,
    private val workoutPrefsDataStore: WorkoutPrefsDataStore,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : WorkoutRepository {

    override suspend fun getWorkoutById(id: String): Workout = withContext(ioDispatcher) {
        workoutApi.getWorkoutById(id).toDomain()
    }

    override fun observeLastWorkoutId(): Flow<String?> = workoutPrefsDataStore.lastWorkoutId

    override suspend fun saveLastWorkoutId(workoutId: String) {
        withContext(ioDispatcher) {
            workoutPrefsDataStore.saveLastWorkoutId(workoutId)
        }
    }
}

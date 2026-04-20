package com.firkat.intervaltraining.core.di

import com.firkat.intervaltraining.BuildConfig
import com.firkat.intervaltraining.core.data.remote.api.WorkoutApi
import com.firkat.intervaltraining.core.data.repository.MockWorkoutRepository
import com.firkat.intervaltraining.core.data.repository.WorkoutRepositoryImpl
import com.firkat.intervaltraining.core.resources.StringProvider
import com.firkat.intervaltraining.domain.repository.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWorkoutRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        stringProvider: StringProvider,
        workoutApiProvider: Provider<WorkoutApi>,
    ): WorkoutRepository =
        if (BuildConfig.MOCK_ENABLED) {
            MockWorkoutRepository(
                ioDispatcher = ioDispatcher,
                stringProvider = stringProvider,
            )
        } else {
            WorkoutRepositoryImpl(
                workoutApi = workoutApiProvider.get(),
                ioDispatcher = ioDispatcher,
            )
        }
}

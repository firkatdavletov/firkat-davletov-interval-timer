package com.firkat.intervaltraining.core.di

import com.firkat.intervaltraining.core.data.datastore.WorkoutPrefsDataStore
import com.firkat.intervaltraining.core.data.datastore.WorkoutPrefsDataStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutPrefsDataStore(impl: WorkoutPrefsDataStoreImpl): WorkoutPrefsDataStore
}

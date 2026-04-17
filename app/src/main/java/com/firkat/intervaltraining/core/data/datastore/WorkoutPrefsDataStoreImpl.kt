package com.firkat.intervaltraining.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class WorkoutPrefsDataStoreImpl @Inject constructor(
    @ApplicationContext context: Context,
) : WorkoutPrefsDataStore {

    private val dataStore: DataStore<Preferences> = androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(FILE_NAME) }
    )

    override val lastWorkoutId: Flow<String?> = dataStore.data.map { prefs -> prefs[LAST_WORKOUT_ID_KEY] }

    override suspend fun saveLastWorkoutId(workoutId: String) {
        dataStore.edit { prefs ->
            prefs[LAST_WORKOUT_ID_KEY] = workoutId
        }
    }

    private companion object {
        const val FILE_NAME = "workout_prefs.preferences_pb"
        val LAST_WORKOUT_ID_KEY = stringPreferencesKey("last_workout_id")
    }
}

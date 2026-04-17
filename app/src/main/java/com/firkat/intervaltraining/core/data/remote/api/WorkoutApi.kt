package com.firkat.intervaltraining.core.data.remote.api

import com.firkat.intervaltraining.core.data.remote.dto.WorkoutDto
import retrofit2.http.GET
import retrofit2.http.Path

interface WorkoutApi {

    @GET("api/interval-timers/{id}")
    suspend fun getWorkoutById(@Path("id") id: String): WorkoutDto
}

package com.firkat.intervaltraining.core.data.remote.dto

import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.core.model.Workout
import com.google.gson.annotations.SerializedName

data class WorkoutResponseDto(
    @SerializedName("timer")
    val timer: TimerDto,
)

data class TimerDto(
    @SerializedName("timer_id")
    val timerId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("total_time")
    val totalTime: Int,
    @SerializedName("intervals")
    val intervals: List<TimerIntervalDto>,
)

data class TimerIntervalDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("time")
    val time: Int,
)

fun WorkoutResponseDto.toDomain(): Workout = Workout(
    id = timer.timerId.toString(),
    title = timer.title,
    warmupSeconds = 0,
    cooldownSeconds = 0,
    intervals = timer.intervals.map { interval ->
        IntervalSegment(
            name = interval.title,
            durationSeconds = interval.time,
            targetPace = "",
        )
    },
)

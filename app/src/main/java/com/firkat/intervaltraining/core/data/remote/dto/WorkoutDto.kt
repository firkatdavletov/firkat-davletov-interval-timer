package com.firkat.intervaltraining.core.data.remote.dto

import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.core.model.Workout

data class WorkoutDto(
    val id: String,
    val title: String,
    val warmupSeconds: Int,
    val cooldownSeconds: Int,
    val intervals: List<IntervalSegmentDto>,
)

data class IntervalSegmentDto(
    val name: String,
    val durationSeconds: Int,
    val targetPace: String,
)

fun WorkoutDto.toDomain(): Workout = Workout(
    id = id,
    title = title,
    warmupSeconds = warmupSeconds,
    cooldownSeconds = cooldownSeconds,
    intervals = intervals.map {
        IntervalSegment(
            name = it.name,
            durationSeconds = it.durationSeconds,
            targetPace = it.targetPace,
        )
    },
)

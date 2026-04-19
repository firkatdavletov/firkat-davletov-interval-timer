package com.firkat.intervaltraining.core.model

data class Workout(
    val id: String,
    val title: String,
    val totalTime: Int,
    val elapsedTime: Int,
    val intervals: List<IntervalSegment>,
)

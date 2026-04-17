package com.firkat.intervaltraining.core.model

data class Workout(
    val id: String,
    val title: String,
    val warmupSeconds: Int,
    val cooldownSeconds: Int,
    val intervals: List<IntervalSegment>,
)

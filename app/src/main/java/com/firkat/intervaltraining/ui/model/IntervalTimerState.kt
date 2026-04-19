package com.firkat.intervaltraining.ui.model

sealed interface IntervalTimerState {

    data object Pending : IntervalTimerState

    data object Started : IntervalTimerState

    data object Paused : IntervalTimerState

    data object Completed : IntervalTimerState
}

package com.firkat.intervaltraining.feature.training.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.usecase.GetWorkoutByIdUseCase
import com.firkat.intervaltraining.feature.training.sound.TimerSoundPlayer
import com.firkat.intervaltraining.feature.training.timer.TimerClock
import com.firkat.intervaltraining.ui.model.IntervalTimerState
import com.firkat.intervaltraining.ui.model.WorkoutTimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    private val timerClock: TimerClock,
    private val timerSoundPlayer: TimerSoundPlayer,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null
    private var soundSignalsJob: Job? = null

    init {
        val workoutId = savedStateHandle.get<String>(WORKOUT_ID_ARG).orEmpty()
        if (workoutId.isBlank()) {
            _uiState.update {
                it.copy(isLoading = false)
            }
        } else {
            loadWorkout(workoutId)
        }
    }

    fun onAction(action: TrainingAction) {
        when (action) {
            TrainingAction.StartPauseClicked -> toggleTimer()
            TrainingAction.ResetClicked -> resetWorkout()
            TrainingAction.RefreshTimer -> refreshStartedTimer()
        }
    }

    private fun loadWorkout(workoutId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    workoutId = workoutId,
                    isLoading = true,
                )
            }

            runCatching { getWorkoutByIdUseCase(workoutId) }
                .onSuccess { workout ->
                    val totalSeconds = workout.calculateTotalSeconds()
                    val restoredStatus = restoreTimerStatus(workout.id, workout.intervals, totalSeconds)
                    val restoredElapsedSeconds = restoreElapsedSeconds(restoredStatus, totalSeconds)
                    val resolvedStatus = restoredStatus.resolveFor(restoredElapsedSeconds, totalSeconds)
                    val elapsedSeconds = when (resolvedStatus) {
                        TimerStatus.Completed -> totalSeconds
                        else -> restoredElapsedSeconds.coerceIn(0, totalSeconds)
                    }

                    _uiState.value = TrainingUiState(
                        workoutId = workout.id,
                        workoutTitle = workout.title,
                        segments = workout.intervals,
                        workoutTotalSeconds = totalSeconds,
                        isLoading = false,
                    ).withTimerProgress(
                        status = resolvedStatus,
                        elapsedSeconds = elapsedSeconds,
                    )

                    when (resolvedStatus) {
                        TimerStatus.Started -> {
                            val startedAtRealtimeMillis = timerClock.elapsedRealtimeMillis()
                            persistTimerState(
                                workoutId = workout.id,
                                status = resolvedStatus,
                                elapsedSeconds = elapsedSeconds,
                                startedAtRealtimeMillis = startedAtRealtimeMillis,
                            )
                            startTicker()
                        }

                        TimerStatus.Completed,
                        TimerStatus.Paused,
                        TimerStatus.Pending -> {
                            persistTimerState(
                                workoutId = workout.id,
                                status = resolvedStatus,
                                elapsedSeconds = elapsedSeconds,
                                startedAtRealtimeMillis = null,
                            )
                        }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    private fun toggleTimer() {
        val state = _uiState.value
        if (state.isLoading || state.segments.isEmpty() || state.workoutTotalSeconds <= 0) return

        when (state.workoutTimerState) {
            WorkoutTimerState.Pending,
            WorkoutTimerState.Paused -> {
                startOrResumeWorkout()
            }

            WorkoutTimerState.Started -> pauseWorkout()
            WorkoutTimerState.Completed -> startWorkoutFromBeginning()
        }
    }

    private fun startOrResumeWorkout() {
        val state = _uiState.value
        val elapsedSeconds = state.elapsedSeconds.coerceIn(0, state.workoutTotalSeconds)
        val startedAtRealtimeMillis = timerClock.elapsedRealtimeMillis()
        val shouldPlayStartSignal =
            state.workoutTimerState == WorkoutTimerState.Pending &&
                elapsedSeconds == 0

        persistTimerState(
            workoutId = state.workoutId,
            status = TimerStatus.Started,
            elapsedSeconds = elapsedSeconds,
            startedAtRealtimeMillis = startedAtRealtimeMillis,
        )
        _uiState.value = state.withTimerProgress(
            status = TimerStatus.Started,
            elapsedSeconds = elapsedSeconds,
        )
        if (shouldPlayStartSignal) {
            timerSoundPlayer.playSignal()
        }
        startTicker()
    }

    private fun startWorkoutFromBeginning() {
        stopSoundSignals()
        persistTimerState(
            workoutId = _uiState.value.workoutId,
            status = TimerStatus.Pending,
            elapsedSeconds = 0,
            startedAtRealtimeMillis = null,
        )
        _uiState.value = _uiState.value.withTimerProgress(
            status = TimerStatus.Pending,
            elapsedSeconds = 0,
        )
        startOrResumeWorkout()
    }

    private fun pauseWorkout() {
        val state = _uiState.value
        val elapsedSeconds = currentElapsedSeconds(state.workoutTotalSeconds)

        persistTimerState(
            workoutId = state.workoutId,
            status = TimerStatus.Paused,
            elapsedSeconds = elapsedSeconds,
            startedAtRealtimeMillis = null,
        )
        _uiState.value = state.withTimerProgress(
            status = TimerStatus.Paused,
            elapsedSeconds = elapsedSeconds,
        )
        stopTicker()
    }

    private fun resetWorkout() {
        val state = _uiState.value

        persistTimerState(
            workoutId = state.workoutId,
            status = TimerStatus.Pending,
            elapsedSeconds = 0,
            startedAtRealtimeMillis = null,
        )
        _uiState.value = state.withTimerProgress(
            status = TimerStatus.Pending,
            elapsedSeconds = 0,
        )
        stopSoundSignals()
        stopTicker()
    }

    private fun startTicker() {
        if (tickerJob?.isActive == true) return

        tickerJob = viewModelScope.launch {
            while (isActive) {
                delay(TICK_INTERVAL_MILLIS)
                refreshStartedTimer()
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    private fun refreshStartedTimer() {
        val state = _uiState.value
        if (state.workoutTimerState != WorkoutTimerState.Started) return

        val elapsedSeconds = currentElapsedSeconds(state.workoutTotalSeconds)
        if (elapsedSeconds >= state.workoutTotalSeconds) {
            completeWorkout()
            return
        }

        val updatedState = state.withTimerProgress(
            status = TimerStatus.Started,
            elapsedSeconds = elapsedSeconds,
        )
        _uiState.value = updatedState
        playIntervalTransitionSignals(
            previousSegmentIndex = state.currentSegmentIndex,
            currentSegmentIndex = updatedState.currentSegmentIndex,
        )
    }

    private fun completeWorkout() {
        val state = _uiState.value

        persistTimerState(
            workoutId = state.workoutId,
            status = TimerStatus.Completed,
            elapsedSeconds = state.workoutTotalSeconds,
            startedAtRealtimeMillis = null,
        )
        _uiState.value = state.withTimerProgress(
            status = TimerStatus.Completed,
            elapsedSeconds = state.workoutTotalSeconds,
        )
        stopTicker()
        playCompletionSignals()
    }

    private fun playIntervalTransitionSignals(
        previousSegmentIndex: Int,
        currentSegmentIndex: Int,
    ) {
        val signalsCount = (currentSegmentIndex - previousSegmentIndex).coerceAtLeast(0)
        playSoundSignals(signalsCount)
    }

    private fun playCompletionSignals() {
        playSoundSignals(COMPLETION_SIGNAL_COUNT)
    }

    private fun playSoundSignals(count: Int) {
        if (count <= 0) return
        soundSignalsJob?.cancel()

        if (count == 1) {
            timerSoundPlayer.playSignal()
            return
        }

        soundSignalsJob = viewModelScope.launch {
            repeat(count) { index ->
                timerSoundPlayer.playSignal()
                if (index < count - 1) {
                    delay(SIGNAL_REPEAT_DELAY_MILLIS)
                }
            }
            soundSignalsJob = null
        }
    }

    private fun stopSoundSignals() {
        soundSignalsJob?.cancel()
        soundSignalsJob = null
    }

    private fun restoreTimerStatus(
        workoutId: String,
        intervals: List<IntervalSegment>,
        totalSeconds: Int,
    ): TimerStatus {
        if (intervals.isEmpty() || totalSeconds <= 0) return TimerStatus.Pending
        if (savedStateHandle.get<String>(KEY_SAVED_WORKOUT_ID) != workoutId) return TimerStatus.Pending
        return savedStateHandle.get<String>(KEY_TIMER_STATUS)
            ?.let(TimerStatus::fromName)
            ?: TimerStatus.Pending
    }

    private fun restoreElapsedSeconds(
        status: TimerStatus,
        totalSeconds: Int,
    ): Int {
        val baseElapsedSeconds = savedBaseElapsedSeconds().coerceIn(0, totalSeconds)
        return when (status) {
            TimerStatus.Started -> {
                val runningSeconds = (
                    timerClock.elapsedRealtimeMillis() - savedStartedAtRealtimeMillis()
                    ).coerceAtLeast(0L) / MILLIS_IN_SECOND
                (baseElapsedSeconds + runningSeconds.toInt()).coerceIn(0, totalSeconds)
            }

            TimerStatus.Paused,
            TimerStatus.Completed,
            TimerStatus.Pending -> {
                baseElapsedSeconds
            }
        }
    }

    private fun currentElapsedSeconds(totalSeconds: Int): Int {
        val baseElapsedSeconds = savedBaseElapsedSeconds().coerceIn(0, totalSeconds)
        val startedAtRealtimeMillis = savedStartedAtRealtimeMillis()
        val runningSeconds = (
            timerClock.elapsedRealtimeMillis() - startedAtRealtimeMillis
            ).coerceAtLeast(0L) / MILLIS_IN_SECOND

        return (baseElapsedSeconds + runningSeconds.toInt()).coerceIn(0, totalSeconds)
    }

    private fun persistTimerState(
        workoutId: String,
        status: TimerStatus,
        elapsedSeconds: Int,
        startedAtRealtimeMillis: Long?,
    ) {
        savedStateHandle[KEY_SAVED_WORKOUT_ID] = workoutId
        savedStateHandle[KEY_TIMER_STATUS] = status.name
        savedStateHandle[KEY_BASE_ELAPSED_SECONDS] = elapsedSeconds
        savedStateHandle[KEY_STARTED_AT_REALTIME_MILLIS] = startedAtRealtimeMillis ?: 0L
    }

    private fun savedBaseElapsedSeconds(): Int =
        savedStateHandle.get<Int>(KEY_BASE_ELAPSED_SECONDS) ?: 0

    private fun savedStartedAtRealtimeMillis(): Long =
        savedStateHandle.get<Long>(KEY_STARTED_AT_REALTIME_MILLIS) ?: timerClock.elapsedRealtimeMillis()

    private fun Workout.calculateTotalSeconds(): Int {
        val intervalTotalSeconds = intervals.sumOf { it.totalSeconds.coerceAtLeast(0) }
        return intervalTotalSeconds.takeIf { it > 0 } ?: totalTime.coerceAtLeast(0)
    }

    private fun TimerStatus.resolveFor(
        elapsedSeconds: Int,
        totalSeconds: Int,
    ): TimerStatus = when {
        this != TimerStatus.Pending && totalSeconds > 0 && elapsedSeconds >= totalSeconds -> TimerStatus.Completed
        else -> this
    }

    private fun TrainingUiState.withTimerProgress(
        status: TimerStatus,
        elapsedSeconds: Int,
    ): TrainingUiState {
        val normalizedElapsedSeconds = elapsedSeconds.coerceIn(0, workoutTotalSeconds)
        val segmentsWithProgress = segments.withElapsedProgress(normalizedElapsedSeconds)
        return copy(
            segments = segmentsWithProgress,
            workoutTimerState = status.toWorkoutTimerState(),
            timerState = status.toIntervalTimerState(),
            currentSegmentIndex = segments.currentSegmentIndex(
                elapsedSeconds = normalizedElapsedSeconds,
                isCompleted = status == TimerStatus.Completed,
            ),
            elapsedSeconds = normalizedElapsedSeconds,
            isRunning = status == TimerStatus.Started,
        )
    }

    private fun List<IntervalSegment>.withElapsedProgress(totalElapsedSeconds: Int): List<IntervalSegment> {
        var remainingElapsedSeconds = totalElapsedSeconds
        return map { segment ->
            val segmentDuration = segment.totalSeconds.coerceAtLeast(0)
            val segmentElapsedSeconds = remainingElapsedSeconds.coerceIn(0, segmentDuration)
            remainingElapsedSeconds = (remainingElapsedSeconds - segmentDuration).coerceAtLeast(0)
            segment.copy(elapsedSeconds = segmentElapsedSeconds)
        }
    }

    private fun List<IntervalSegment>.currentSegmentIndex(
        elapsedSeconds: Int,
        isCompleted: Boolean,
    ): Int {
        if (isEmpty()) return 0
        if (isCompleted) return lastIndex

        var accumulatedSeconds = 0
        forEachIndexed { index, segment ->
            accumulatedSeconds += segment.totalSeconds.coerceAtLeast(0)
            if (elapsedSeconds < accumulatedSeconds) return index
        }
        return lastIndex
    }

    private enum class TimerStatus {
        Pending,
        Started,
        Paused,
        Completed,
        ;

        fun toWorkoutTimerState(): WorkoutTimerState = when (this) {
            Pending -> WorkoutTimerState.Pending
            Started -> WorkoutTimerState.Started
            Paused -> WorkoutTimerState.Paused
            Completed -> WorkoutTimerState.Completed
        }

        fun toIntervalTimerState(): IntervalTimerState = when (this) {
            Pending -> IntervalTimerState.Pending
            Started -> IntervalTimerState.Started
            Paused -> IntervalTimerState.Paused
            Completed -> IntervalTimerState.Completed
        }

        companion object {
            fun fromName(name: String): TimerStatus? =
                entries.firstOrNull { it.name == name }
        }
    }

    companion object {
        const val WORKOUT_ID_ARG = "workoutId"

        private const val KEY_SAVED_WORKOUT_ID = "training.timer.workout_id"
        private const val KEY_TIMER_STATUS = "training.timer.status"
        private const val KEY_BASE_ELAPSED_SECONDS = "training.timer.base_elapsed_seconds"
        private const val KEY_STARTED_AT_REALTIME_MILLIS = "training.timer.started_at_realtime_millis"
        private const val TICK_INTERVAL_MILLIS = 1_000L
        private const val MILLIS_IN_SECOND = 1_000L
        private const val SIGNAL_REPEAT_DELAY_MILLIS = 250L
        private const val COMPLETION_SIGNAL_COUNT = 2
    }
}

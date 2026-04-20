package com.firkat.intervaltraining.feature.training.presentation

import androidx.lifecycle.SavedStateHandle
import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.usecase.GetWorkoutByIdUseCase
import com.firkat.intervaltraining.feature.training.sound.TimerSoundPlayer
import com.firkat.intervaltraining.feature.training.timer.TimerClock
import com.firkat.intervaltraining.fakes.FakeWorkoutRepository
import com.firkat.intervaltraining.testutil.FakeStringProvider
import com.firkat.intervaltraining.testutil.MainDispatcherRule
import com.firkat.intervaltraining.ui.model.IntervalTimerState
import com.firkat.intervaltraining.ui.model.WorkoutTimerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrainingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val timerClock = FakeTimerClock()
    private val timerSoundPlayer = FakeTimerSoundPlayer()

    private val repository = FakeWorkoutRepository(
        mutableMapOf(
            TEST_WORKOUT_ID to Workout(
                id = TEST_WORKOUT_ID,
                title = "Track Session",
                totalTime = 4,
                elapsedTime = 0,
                intervals = listOf(
                    IntervalSegment(name = "Fast 1", totalSeconds = 2, elapsedSeconds = 0),
                    IntervalSegment(name = "Recovery", totalSeconds = 2, elapsedSeconds = 0),
                ),
            )
        )
    )

    @Test
    fun `loads workout on init`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Track Session", state.workoutTitle)
        assertEquals(2, state.segments.size)
        assertEquals(4, state.workoutTotalSeconds)
        assertEquals(0, state.elapsedSeconds)
        assertEquals(WorkoutTimerState.Pending, state.workoutTimerState)
        assertFalse(state.isLoading)
    }

    @Test
    fun `sets error message when workout id is missing`() = runTest {
        val viewModel = createViewModel(SavedStateHandle())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Workout id не найден", state.errorMessage)
    }

    @Test
    fun `sets error message when workout loading fails`() = runTest {
        repository.shouldThrow = true

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Не удалось загрузить тренировку", state.errorMessage)
    }

    @Test
    fun `start action starts timer and updates current interval progress`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        runCurrent()
        assertTrue(viewModel.uiState.value.isRunning)
        assertEquals(WorkoutTimerState.Started, viewModel.uiState.value.workoutTimerState)

        timerClock.advanceBy(1_000L)
        advanceTimeBy(1_000L)
        runCurrent()
        assertEquals(1, viewModel.uiState.value.elapsedSeconds)
        assertEquals(1, viewModel.uiState.value.segments[0].elapsedSeconds)
        assertEquals(0, viewModel.uiState.value.currentSegmentIndex)
        assertEquals(IntervalTimerState.Started, viewModel.uiState.value.timerState)

        viewModel.onAction(TrainingAction.StartPauseClicked)
        runCurrent()
        assertFalse(viewModel.uiState.value.isRunning)
        assertEquals(WorkoutTimerState.Paused, viewModel.uiState.value.workoutTimerState)
    }

    @Test
    fun `timer moves to next interval when current interval finishes`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        timerClock.advanceBy(2_000L)
        advanceTimeBy(2_000L)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(2, state.elapsedSeconds)
        assertEquals(1, state.currentSegmentIndex)
        assertEquals(2, state.segments[0].elapsedSeconds)
        assertEquals(0, state.segments[1].elapsedSeconds)
        assertEquals(WorkoutTimerState.Started, state.workoutTimerState)

        viewModel.onAction(TrainingAction.ResetClicked)
        runCurrent()
    }

    @Test
    fun `pause keeps elapsed time fixed while virtual time continues`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        timerClock.advanceBy(1_000L)
        advanceTimeBy(1_000L)
        runCurrent()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        timerClock.advanceBy(3_000L)
        advanceTimeBy(3_000L)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(1, state.elapsedSeconds)
        assertEquals(WorkoutTimerState.Paused, state.workoutTimerState)
        assertFalse(state.isRunning)
    }

    @Test
    fun `recreated view model restores actual running timer progress`() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(TrainingViewModel.WORKOUT_ID_ARG to TEST_WORKOUT_ID)
        )
        val firstViewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        firstViewModel.onAction(TrainingAction.StartPauseClicked)
        runCurrent()

        timerClock.advanceBy(3_000L)

        val restoredViewModel = createViewModel(savedStateHandle)
        runCurrent()

        val state = restoredViewModel.uiState.value
        assertEquals(3, state.elapsedSeconds)
        assertEquals(1, state.currentSegmentIndex)
        assertEquals(2, state.segments[0].elapsedSeconds)
        assertEquals(1, state.segments[1].elapsedSeconds)
        assertEquals(WorkoutTimerState.Started, state.workoutTimerState)

        firstViewModel.onAction(TrainingAction.ResetClicked)
        restoredViewModel.onAction(TrainingAction.ResetClicked)
        runCurrent()
    }

    @Test
    fun `refresh action recalculates progress without waiting for next ticker delay`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        runCurrent()

        timerClock.advanceBy(3_000L)
        viewModel.onAction(TrainingAction.RefreshTimer)

        val state = viewModel.uiState.value
        assertEquals(3, state.elapsedSeconds)
        assertEquals(1, state.currentSegmentIndex)
        assertEquals(1, state.segments[1].elapsedSeconds)

        viewModel.onAction(TrainingAction.ResetClicked)
        runCurrent()
    }

    @Test
    fun `timer completes workout`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        timerClock.advanceBy(4_000L)
        advanceTimeBy(4_000L)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(4, state.elapsedSeconds)
        assertEquals(1, state.currentSegmentIndex)
        assertEquals(WorkoutTimerState.Completed, state.workoutTimerState)
        assertEquals(IntervalTimerState.Completed, state.timerState)
        assertFalse(state.isRunning)
    }

    @Test
    fun `start action plays start signal only when training starts from beginning`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        runCurrent()

        assertEquals(1, timerSoundPlayer.signalCount)

        viewModel.onAction(TrainingAction.StartPauseClicked)
        runCurrent()
        viewModel.onAction(TrainingAction.StartPauseClicked)
        runCurrent()

        assertEquals(1, timerSoundPlayer.signalCount)

        viewModel.onAction(TrainingAction.ResetClicked)
        runCurrent()
    }

    @Test
    fun `timer plays signal when moving to next interval`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        timerClock.advanceBy(2_000L)
        advanceTimeBy(2_000L)
        runCurrent()

        assertEquals(2, timerSoundPlayer.signalCount)

        viewModel.onAction(TrainingAction.ResetClicked)
        runCurrent()
    }

    @Test
    fun `timer plays two completion signals when workout completes`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        timerClock.advanceBy(2_000L)
        advanceTimeBy(2_000L)
        runCurrent()
        timerClock.advanceBy(2_000L)
        advanceTimeBy(1_000L)
        runCurrent()

        assertEquals(3, timerSoundPlayer.signalCount)

        advanceTimeBy(250L)
        runCurrent()

        assertEquals(4, timerSoundPlayer.signalCount)
    }

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle(
            mapOf(TrainingViewModel.WORKOUT_ID_ARG to TEST_WORKOUT_ID)
        ),
    ): TrainingViewModel {
        return TrainingViewModel(
            savedStateHandle = savedStateHandle,
            getWorkoutByIdUseCase = GetWorkoutByIdUseCase(repository),
            timerClock = timerClock,
            timerSoundPlayer = timerSoundPlayer,
            stringProvider = FakeStringProvider(),
        )
    }

    private class FakeTimerClock : TimerClock {
        private var elapsedRealtimeMillis = 0L

        override fun elapsedRealtimeMillis(): Long = elapsedRealtimeMillis

        fun advanceBy(millis: Long) {
            elapsedRealtimeMillis += millis
        }
    }

    private class FakeTimerSoundPlayer : TimerSoundPlayer {
        var signalCount = 0
            private set

        override fun playSignal() {
            signalCount++
        }
    }

    private companion object {
        const val TEST_WORKOUT_ID = "workout_1"
    }
}

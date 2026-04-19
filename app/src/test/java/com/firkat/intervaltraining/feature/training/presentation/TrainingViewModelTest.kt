package com.firkat.intervaltraining.feature.training.presentation

import androidx.lifecycle.SavedStateHandle
import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.usecase.GetWorkoutByIdUseCase
import com.firkat.intervaltraining.fakes.FakeWorkoutRepository
import com.firkat.intervaltraining.testutil.MainDispatcherRule
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
        assertFalse(state.isLoading)
    }

    @Test
    fun `start action sets running and timer ticks`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TrainingAction.StartPauseClicked)
        runCurrent()
        assertTrue(viewModel.uiState.value.isRunning)

        advanceTimeBy(1_000L)
        runCurrent()
        assertEquals(1, viewModel.uiState.value.elapsedSeconds)

        viewModel.onAction(TrainingAction.StartPauseClicked)
        runCurrent()
        assertFalse(viewModel.uiState.value.isRunning)
    }

    private fun createViewModel(): TrainingViewModel {
        return TrainingViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(TrainingViewModel.WORKOUT_ID_ARG to TEST_WORKOUT_ID)
            ),
            getWorkoutByIdUseCase = GetWorkoutByIdUseCase(repository),
        )
    }

    private companion object {
        const val TEST_WORKOUT_ID = "workout_1"
    }
}

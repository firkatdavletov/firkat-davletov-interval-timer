package com.firkat.intervaltraining.feature.loadworkout.presentation

import app.cash.turbine.test
import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.core.model.Workout
import com.firkat.intervaltraining.domain.usecase.GetWorkoutByIdUseCase
import com.firkat.intervaltraining.domain.usecase.ObserveLastWorkoutIdUseCase
import com.firkat.intervaltraining.domain.usecase.SaveLastWorkoutIdUseCase
import com.firkat.intervaltraining.fakes.FakeWorkoutRepository
import com.firkat.intervaltraining.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoadWorkoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWorkoutRepository(
        mutableMapOf(
            TEST_WORKOUT_ID to Workout(
                id = TEST_WORKOUT_ID,
                title = "Evening Intervals",
                warmupSeconds = 300,
                cooldownSeconds = 300,
                intervals = listOf(
                    IntervalSegment(name = "Fast", durationSeconds = 60, targetPace = "4:30"),
                ),
            )
        )
    )

    private val viewModel = LoadWorkoutViewModel(
        getWorkoutByIdUseCase = GetWorkoutByIdUseCase(repository),
        saveLastWorkoutIdUseCase = SaveLastWorkoutIdUseCase(repository),
        observeLastWorkoutIdUseCase = ObserveLastWorkoutIdUseCase(repository),
    )

    @Test
    fun `when workout id changes then ui state is updated`() {
        viewModel.onAction(LoadWorkoutAction.WorkoutIdChanged("42"))

        assertEquals("42", viewModel.uiState.value.workoutIdInput)
    }

    @Test
    fun `when submit clicked and workout exists then emits navigation event`() = runTest {
        viewModel.onAction(LoadWorkoutAction.WorkoutIdChanged(TEST_WORKOUT_ID))

        viewModel.events.test {
            viewModel.onAction(LoadWorkoutAction.SubmitClicked)
            advanceUntilIdle()

            val event = awaitItem()
            assertEquals(
                LoadWorkoutEvent.NavigateToTraining(TEST_WORKOUT_ID),
                event,
            )
        }
    }

    private companion object {
        const val TEST_WORKOUT_ID = "workout_1"
    }
}

package com.firkat.intervaltraining.feature.loadworkout.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutAction
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutUiState
import com.firkat.intervaltraining.ui.theme.IntervalTrainingTheme
import org.junit.Rule
import org.junit.Test

class LoadWorkoutScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadButton_enabled_afterIdEntered() {
        composeRule.setContent {
            IntervalTrainingTheme {
                val state = remember { mutableStateOf(LoadWorkoutUiState()) }
                LoadWorkoutScreen(
                    state = state.value,
                    onAction = { action ->
                        when (action) {
                            is LoadWorkoutAction.WorkoutIdChanged -> {
                                state.value = state.value.copy(workoutIdInput = action.value)
                            }

                            else -> Unit
                        }
                    },
                )
            }
        }

        composeRule.onNodeWithTag(LOAD_WORKOUT_ID_INPUT_TAG)
            .performTextInput("workout-7")

        composeRule.onNodeWithTag(LOAD_WORKOUT_BUTTON_TAG)
            .assertIsEnabled()
    }
}

package com.firkat.intervaltraining.feature.loadworkout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutAction
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutEvent
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutUiState
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutViewModel

@Composable
fun LoadWorkoutRoute(
    onNavigateToTraining: (String) -> Unit,
    viewModel: LoadWorkoutViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoadWorkoutEvent.NavigateToTraining -> onNavigateToTraining(event.workoutId)
            }
        }
    }

    LoadWorkoutScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LoadWorkoutScreen(
    state: LoadWorkoutUiState,
    onAction: (LoadWorkoutAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onAction(LoadWorkoutAction.ClearErrorClicked)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(text = "Загрузка тренировки") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.workoutIdInput,
                onValueChange = { onAction(LoadWorkoutAction.WorkoutIdChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(LOAD_WORKOUT_ID_INPUT_TAG),
                label = { Text(text = "Workout id") },
                singleLine = true,
            )

            Button(
                onClick = { onAction(LoadWorkoutAction.SubmitClicked) },
                enabled = !state.isLoading && state.workoutIdInput.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(LOAD_WORKOUT_BUTTON_TAG),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = "Загрузить")
                }
            }

            state.lastLoadedWorkoutTitle?.let { title ->
                Text(
                    text = "Последняя загруженная: $title",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            TextButton(onClick = { onAction(LoadWorkoutAction.WorkoutIdChanged("")) }) {
                Text(text = "Очистить")
            }
        }
    }
}

const val LOAD_WORKOUT_ID_INPUT_TAG = "loadWorkoutIdInput"
const val LOAD_WORKOUT_BUTTON_TAG = "loadWorkoutButton"

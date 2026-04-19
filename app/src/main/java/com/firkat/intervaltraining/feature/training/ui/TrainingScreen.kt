package com.firkat.intervaltraining.feature.training.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firkat.intervaltraining.feature.training.presentation.TrainingAction
import com.firkat.intervaltraining.feature.training.presentation.TrainingUiState
import com.firkat.intervaltraining.feature.training.presentation.TrainingViewModel

@Composable
fun TrainingRoute(
    viewModel: TrainingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TrainingScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TrainingScreen(
    state: TrainingUiState,
    onAction: (TrainingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onAction(TrainingAction.DismissError)
        }
    }

    val currentSegment = state.segments.getOrNull(state.currentSegmentIndex)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(text = "Тренировка") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = state.workoutTitle.ifBlank { "Загрузка..." },
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(text = "ID: ${state.workoutId}")

            if (currentSegment != null) {
                Text(text = "Текущий интервал: ${currentSegment.name}")
                if (currentSegment.targetPace.isNotBlank()) {
                    Text(text = "Пейс: ${currentSegment.targetPace}")
                }
                Text(text = "Осталось: ${state.secondsLeftInSegment} сек")
                Text(text = "Интервал ${state.currentSegmentIndex + 1}/${state.segments.size}")
            } else {
                Text(text = "Интервалы отсутствуют")
            }

            Button(
                onClick = { onAction(TrainingAction.StartPauseClicked) },
                enabled = !state.isLoading && state.segments.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = if (state.isRunning) "Пауза" else "Старт")
            }

            Button(
                onClick = { onAction(TrainingAction.ResetClicked) },
                enabled = state.segments.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Сброс")
            }
        }
    }
}

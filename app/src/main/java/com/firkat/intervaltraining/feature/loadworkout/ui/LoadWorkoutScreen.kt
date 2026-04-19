package com.firkat.intervaltraining.feature.loadworkout.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firkat.intervaltraining.R
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutAction
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutEvent
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutUiState
import com.firkat.intervaltraining.feature.loadworkout.presentation.LoadWorkoutViewModel
import com.firkat.intervaltraining.ui.components.InputField
import com.firkat.intervaltraining.ui.components.PrimaryButton
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.AppTypography

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AppColor.bg,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(AppSpacing.l),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.l),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(top = 54.dp)
                        .clip(RoundedCornerShape(AppSpacing.l))
                        .background(AppColor.primary)
                        .padding(AppSpacing.m),
            ) {
                Image(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_schedule),
                    contentDescription = null,
                )
            }
            Text(
                text = "Интервальная тренировка",
                style = AppTypography.h1,
                color = AppColor.textPrimary,
            )
            InputField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(LOAD_WORKOUT_ID_INPUT_TAG),
                value = state.workoutIdInput,
                onValueChange = {
                    onAction(LoadWorkoutAction.WorkoutIdChanged(it))
                },
            )

            PrimaryButton(
                onClick = { onAction(LoadWorkoutAction.SubmitClicked) },
                enabled = !state.isLoading,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(LOAD_WORKOUT_BUTTON_TAG),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppSpacing.xxl),
                        strokeWidth = 2.5.dp,
                        color = AppColor.primary.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.width(AppSpacing.s))
                    Text(
                        text = "Загрузка...",
                        style = AppTypography.button,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    Text(
                        text = "Загрузить",
                        style = AppTypography.button,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoadWorkoutScreen_Preview() {
    LoadWorkoutScreen(
        state = LoadWorkoutUiState(),
        onAction = {},
    )
}

const val LOAD_WORKOUT_ID_INPUT_TAG = "loadWorkoutIdInput"
const val LOAD_WORKOUT_BUTTON_TAG = "loadWorkoutButton"

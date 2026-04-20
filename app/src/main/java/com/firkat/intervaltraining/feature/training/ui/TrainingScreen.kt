package com.firkat.intervaltraining.feature.training.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firkat.intervaltraining.R
import com.firkat.intervaltraining.core.model.IntervalSegment
import com.firkat.intervaltraining.feature.training.presentation.TrainingAction
import com.firkat.intervaltraining.feature.training.presentation.TrainingUiState
import com.firkat.intervaltraining.feature.training.presentation.TrainingViewModel
import com.firkat.intervaltraining.ui.components.BackButton
import com.firkat.intervaltraining.ui.components.GhostButton
import com.firkat.intervaltraining.ui.components.IntervalItem
import com.firkat.intervaltraining.ui.components.PrimaryButton
import com.firkat.intervaltraining.ui.components.TimerCard
import com.firkat.intervaltraining.ui.model.IntervalTimerState
import com.firkat.intervaltraining.ui.model.WorkoutTimerState
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.AppTypography
import com.firkat.intervaltraining.util.TimeFormatter

@Composable
fun TrainingRoute(
    onNavigateBack: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TrainingScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onNavigateBack,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TrainingScreen(
    state: TrainingUiState,
    onAction: (TrainingAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    onAction(TrainingAction.RefreshTimer)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier =
                    Modifier
                        .systemBarsPadding()
                        .padding(start = AppSpacing.l, end = AppSpacing.xxl),
                navigationIcon = {
                    BackButton(
                        onClick = onBackClick,
                    )
                },
                title = {
                    Text(
                        text = state.workoutTitle,
                        style = AppTypography.title,
                        color = AppColor.textPrimary,
                    )
                },
                actions = {
                    TrainingTopBarStatus(state = state)
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColor.bg,
                    ),
            )
        },
        containerColor = AppColor.bg,
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = AppSpacing.xxl),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xl),
            ) {
                TrainingTimerSection(state = state)
                IntervalsSection(state = state)
            }

            TrainingBottomActions(
                state = state,
                onAction = onAction,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun TrainingTopBarStatus(state: TrainingUiState) {
    val workoutElapsedSeconds = state.elapsedSeconds.coerceIn(0, state.workoutTotalSeconds)

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (state.workoutTimerState) {
            is WorkoutTimerState.Completed -> {
                Text(
                    text = stringResource(R.string.training_status_completed),
                    style = AppTypography.label,
                    color = AppColor.secondary,
                )
            }

            is WorkoutTimerState.Paused -> {
                Image(
                    modifier = Modifier.size(16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_pause),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(AppColor.orange),
                )
                Spacer(Modifier.width(AppSpacing.xs))
                Text(
                    text = stringResource(R.string.training_status_paused),
                    style = AppTypography.label,
                    color = AppColor.orange,
                )
            }

            is WorkoutTimerState.Pending -> {
                Text(
                    text = TimeFormatter.formatIntervalTime(state.workoutTotalSeconds),
                    style = AppTypography.label,
                    color = AppColor.textSecondary,
                )
            }

            is WorkoutTimerState.Started -> {
                Box(
                    modifier =
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(AppColor.primary),
                )
                Spacer(Modifier.width(AppSpacing.xs))
                Text(
                    text = TimeFormatter.formatIntervalTime(workoutElapsedSeconds),
                    style = AppTypography.label,
                    color = AppColor.primary,
                )
            }
        }
    }
}

@Composable
private fun TrainingTimerSection(state: TrainingUiState) {
    val currentSegment = state.segments.getOrNull(state.currentSegmentIndex)
    val currentSegmentElapsedSeconds = currentSegment?.elapsedSeconds ?: 0
    val currentSegmentTotalSeconds = currentSegment?.totalSeconds ?: 0
    val title =
        when (state.workoutTimerState) {
            is WorkoutTimerState.Completed -> {
                stringResource(R.string.training_completed_title)
            }

            else -> {
                currentSegment?.name ?: if (state.isLoading) {
                    stringResource(R.string.common_loading_short)
                } else {
                    stringResource(R.string.training_no_intervals)
                }
            }
        }
    val timerCardTotalSeconds =
        when (state.workoutTimerState) {
            WorkoutTimerState.Pending,
            WorkoutTimerState.Completed,
            -> state.workoutTotalSeconds

            else -> currentSegmentTotalSeconds
        }
    val timerCardElapsedSeconds =
        when (state.workoutTimerState) {
            WorkoutTimerState.Completed -> state.elapsedSeconds
            WorkoutTimerState.Pending -> 0
            else -> currentSegmentElapsedSeconds
        }

    Column(
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xl),
    ) {
        TimerCard(
            title = title,
            totalSeconds = timerCardTotalSeconds,
            elapsedSeconds = timerCardElapsedSeconds,
            state = state.workoutTimerState,
        )
        state.errorMessage?.let { errorMessage ->
            TrainingErrorMessage(text = errorMessage)
        }
        if (state.workoutTimerState is WorkoutTimerState.Completed) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.s),
            ) {
                WorkoutSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.training_summary_total_time),
                    value = TimeFormatter.formatIntervalTime(state.workoutTotalSeconds),
                )
                WorkoutSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.training_summary_intervals),
                    value = state.segments.size.toString(),
                )
            }
        }
    }
}

@Composable
private fun TrainingErrorMessage(
    text: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, AppColor.error.copy(alpha = 0.16f)),
        colors = CardDefaults.cardColors(containerColor = AppColor.surface),
    ) {
        Text(
            modifier = Modifier.padding(AppSpacing.m),
            text = text,
            style = AppTypography.caption,
            color = AppColor.error,
        )
    }
}

@Composable
private fun IntervalsSection(state: TrainingUiState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppSpacing.s),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.training_intervals_title),
                style = AppTypography.label,
                color = AppColor.textSecondary,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text =
                    stringResource(
                        R.string.training_interval_count,
                        if (state.segments.isEmpty()) 0 else state.currentSegmentIndex + 1,
                        state.segments.size,
                    ),
                style = AppTypography.label,
                color = AppColor.textTertiary,
            )
            if (state.workoutTimerState is WorkoutTimerState.Completed) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check_small),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(AppColor.textTertiary),
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.s),
        ) {
            items(count = state.segments.size) { index ->
                val interval = state.segments[index]
                IntervalItem(
                    index = index + 1,
                    title = interval.name,
                    totalSeconds = interval.totalSeconds,
                    elapsedSeconds = interval.elapsedSeconds,
                    state =
                        state.resolveIntervalState(
                            interval = interval,
                            intervalIndex = index,
                        ),
                )
            }
            item {
                Spacer(modifier = Modifier.height(160.dp))
            }
        }
    }
}

@Composable
private fun TrainingBottomActions(
    state: TrainingUiState,
    onAction: (TrainingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                AppColor.bg.copy(alpha = 0f),
                                AppColor.bg,
                            ),
                    ),
                ).padding(top = 72.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = AppSpacing.xxl, end = AppSpacing.xxl, bottom = AppSpacing.xxl)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.s),
        ) {
            when (state.workoutTimerState) {
                WorkoutTimerState.Pending -> {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onAction(TrainingAction.StartPauseClicked) },
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_play),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AppColor.surface),
                        )
                        Spacer(Modifier.width(AppSpacing.s))
                        Text(
                            text = stringResource(R.string.training_start),
                            style = AppTypography.button,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                is WorkoutTimerState.Started -> {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = AppColor.orange,
                        onClick = { onAction(TrainingAction.StartPauseClicked) },
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_pause),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AppColor.surface),
                        )
                        Spacer(Modifier.width(AppSpacing.s))
                        Text(
                            text = stringResource(R.string.training_pause),
                            style = AppTypography.button,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    ResetTrainingButton(onAction = onAction)
                }

                is WorkoutTimerState.Paused -> {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onAction(TrainingAction.StartPauseClicked) },
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_play),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AppColor.surface),
                        )
                        Spacer(Modifier.width(AppSpacing.s))
                        Text(
                            text = stringResource(R.string.training_resume),
                            style = AppTypography.button,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    ResetTrainingButton(onAction = onAction)
                }

                is WorkoutTimerState.Completed -> {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = AppColor.secondary,
                        onClick = { onAction(TrainingAction.StartPauseClicked) },
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_replay),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AppColor.surface),
                        )
                        Spacer(Modifier.width(AppSpacing.s))
                        Text(
                            text = stringResource(R.string.training_restart),
                            style = AppTypography.button,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    GhostButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.training_new_workout),
                        onClick = { onAction(TrainingAction.ResetClicked) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ResetTrainingButton(onAction: (TrainingAction) -> Unit) {
    GhostButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.training_reset),
        negative = true,
        onClick = { onAction(TrainingAction.ResetClicked) },
    )
}

@Composable
private fun WorkoutSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, AppColor.border),
        colors = CardDefaults.cardColors(containerColor = AppColor.surface),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.l),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = AppTypography.mono,
                color = AppColor.textPrimary,
            )
            Text(
                text = title,
                style = AppTypography.label,
                color = AppColor.textTertiary,
            )
        }
    }
}

private fun TrainingUiState.resolveIntervalState(
    interval: IntervalSegment,
    intervalIndex: Int,
): IntervalTimerState =
    when {
        workoutTimerState is WorkoutTimerState.Completed -> IntervalTimerState.Completed

        workoutTimerState !is WorkoutTimerState.Pending &&
            interval.elapsedSeconds >= interval.totalSeconds -> IntervalTimerState.Completed

        workoutTimerState is WorkoutTimerState.Pending &&
            currentSegmentIndex == intervalIndex -> IntervalTimerState.Selected

        currentSegmentIndex == intervalIndex -> timerState

        else -> IntervalTimerState.Pending
    }

@Preview
@Composable
private fun TrainingScreen_Preview() {
    TrainingScreen(
        state =
            TrainingUiState(
                workoutTitle = stringResource(R.string.preview_workout_title),
                currentSegmentIndex = 0,
                segments =
                    listOf(
                        IntervalSegment(
                            name = stringResource(R.string.preview_interval_warmup),
                            totalSeconds = 60,
                            elapsedSeconds = 0,
                        ),
                        IntervalSegment(
                            name = stringResource(R.string.preview_interval_fast_run),
                            totalSeconds = 45,
                            elapsedSeconds = 0,
                        ),
                        IntervalSegment(
                            name = stringResource(R.string.preview_interval_walk),
                            totalSeconds = 30,
                            elapsedSeconds = 0,
                        ),
                        IntervalSegment(
                            name = stringResource(R.string.preview_interval_jumps),
                            totalSeconds = 40,
                            elapsedSeconds = 0,
                        ),
                        IntervalSegment(
                            name = stringResource(R.string.preview_interval_rest),
                            totalSeconds = 20,
                            elapsedSeconds = 0,
                        ),
                    ),
                elapsedSeconds = 0,
                workoutTotalSeconds = 300,
                workoutTimerState = WorkoutTimerState.Completed,
                timerState = IntervalTimerState.Pending,
            ),
        onAction = {},
        onBackClick = {},
    )
}

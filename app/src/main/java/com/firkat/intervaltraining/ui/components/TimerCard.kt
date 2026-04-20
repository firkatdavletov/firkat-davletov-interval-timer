package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firkat.intervaltraining.R
import com.firkat.intervaltraining.ui.model.WorkoutTimerState
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.AppTypography
import com.firkat.intervaltraining.util.TimeFormatter
import java.util.Locale

@Composable
fun TimerCard(
    modifier: Modifier = Modifier,
    title: String,
    totalSeconds: Int,
    elapsedSeconds: Int,
    state: WorkoutTimerState,
) {
    val safeDurationSec = totalSeconds.coerceAtLeast(0)
    val safeElapsedSec = elapsedSeconds.coerceAtLeast(0)
    val elapsedForProgress = if (safeDurationSec == 0) 0 else safeElapsedSec.coerceAtMost(safeDurationSec)
    val progress =
        if (safeDurationSec == 0 ||
            state is WorkoutTimerState.Pending
        ) {
            0f
        } else {
            elapsedForProgress.toFloat() / safeDurationSec.toFloat()
        }

    val formattedDuration = TimeFormatter.formatIntervalTime(safeDurationSec)
    val formattedElapsed = TimeFormatter.formatIntervalTime(elapsedForProgress)

    val accentColor =
        when (state) {
            is WorkoutTimerState.Completed -> AppColor.secondary
            is WorkoutTimerState.Paused -> AppColor.orange
            WorkoutTimerState.Pending -> AppColor.textPrimary
            is WorkoutTimerState.Started -> AppColor.primary
        }

    val stateLabelTextColor =
        when (state) {
            is WorkoutTimerState.Completed -> AppColor.secondary
            is WorkoutTimerState.Paused -> AppColor.orange
            WorkoutTimerState.Pending -> AppColor.textTertiary
            is WorkoutTimerState.Started -> AppColor.primary
        }

    val titleColor =
        when (state) {
            is WorkoutTimerState.Completed -> AppColor.secondary
            else -> AppColor.textSecondary
        }

    val stateLabel =
        when (state) {
            is WorkoutTimerState.Completed -> stringResource(R.string.timer_card_state_completed)
            is WorkoutTimerState.Paused -> stringResource(R.string.timer_card_state_paused)
            WorkoutTimerState.Pending -> stringResource(R.string.timer_card_state_pending)
            is WorkoutTimerState.Started -> stringResource(R.string.timer_card_state_started)
        }

    val borderColor =
        when (state) {
            is WorkoutTimerState.Completed -> AppColor.secondary
            is WorkoutTimerState.Paused -> AppColor.orange
            WorkoutTimerState.Pending -> AppColor.border
            is WorkoutTimerState.Started -> AppColor.primary
        }

    val cardBackgroundBrush =
        when (state) {
            WorkoutTimerState.Pending -> Brush.verticalGradient(
                colors = listOf(AppColor.surface, AppColor.surface),
            )

            else -> Brush.verticalGradient(
                colors =
                    listOf(
                        accentColor.copy(alpha = 0.04f),
                        AppColor.surface,
                    ),
            )
        }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = AppColor.surface),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(cardBackgroundBrush)
                    .padding(AppSpacing.l),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.s),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stateLabel.uppercase(Locale.ROOT),
                style = AppTypography.state,
                color = stateLabelTextColor,
            )
            Text(
                text = title,
                style = AppTypography.label,
                color = titleColor,
            )
            Text(
                text = when (state) {
                    WorkoutTimerState.Completed -> stringResource(R.string.timer_card_zero_time)
                    WorkoutTimerState.Paused -> formattedElapsed
                    WorkoutTimerState.Pending -> formattedDuration
                    WorkoutTimerState.Started -> formattedElapsed
                },
                style = AppTypography.timerDisplay,
                color = accentColor,
            )
            Text(
                text =
                    when (state) {
                        WorkoutTimerState.Pending -> {
                            stringResource(R.string.timer_card_total_time)
                        }

                        WorkoutTimerState.Completed -> {
                            stringResource(R.string.timer_card_elapsed_of_total, formattedElapsed, formattedDuration)
                        }

                        else -> {
                            stringResource(R.string.timer_card_elapsed_label, formattedElapsed, formattedDuration)
                        }
                    },
                style = AppTypography.caption,
                color = AppColor.textTertiary,
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AppColor.border),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(accentColor),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
private fun TimerCardPreview() {
    ThemePreviewContainer {
        TimerCard(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.preview_interval_slow_run),
            totalSeconds = 5 * 60,
            elapsedSeconds = 0,
            state = WorkoutTimerState.Started,
        )
    }
}

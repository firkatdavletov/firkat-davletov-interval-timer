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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val safeDurationMillis = totalSeconds.coerceAtLeast(0)
    val safeElapsedMillis = elapsedSeconds.coerceAtLeast(0)
    val elapsedForProgress = if (safeDurationMillis == 0) 0 else safeElapsedMillis.coerceAtMost(safeDurationMillis)
    val progress =
        if (safeDurationMillis == 0 ||
            state is WorkoutTimerState.Pending
        ) {
            0f
        } else {
            elapsedForProgress.toFloat() / safeDurationMillis.toFloat()
        }

    val formattedElapsed = TimeFormatter.formatIntervalTime(safeElapsedMillis)
    val formattedDuration = TimeFormatter.formatIntervalTime(safeDurationMillis)

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
            is WorkoutTimerState.Completed -> "Тренировка завершена"
            is WorkoutTimerState.Paused -> "На паузе"
            WorkoutTimerState.Pending -> "Готово к старту"
            is WorkoutTimerState.Started -> "Выполняется"
        }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, AppColor.border),
        colors = CardDefaults.cardColors(containerColor = AppColor.surface),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
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
                    WorkoutTimerState.Completed -> "00:00"
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
                            "Общее время"
                        }

                        else -> {
                            "Прошло $formattedElapsed из $formattedDuration"
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
            title = "Медленный бег",
            totalSeconds = 5 * 60,
            elapsedSeconds = 0,
            state = WorkoutTimerState.Pending,
        )
    }
}

package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firkat.intervaltraining.ui.model.IntervalTimerState
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.AppTypography
import com.firkat.intervaltraining.util.TimeFormatter

@Composable
fun IntervalItem(
    modifier: Modifier = Modifier,
    index: Int,
    title: String,
    totalSeconds: Int,
    elapsedSeconds: Int,
    state: IntervalTimerState,
) {
    val safeDurationMillis = totalSeconds.coerceAtLeast(0)
    val safeElapsedMillis = elapsedSeconds.coerceAtLeast(0)
    val elapsedForProgress = if (safeDurationMillis == 0) 0 else safeElapsedMillis.coerceAtMost(safeDurationMillis)
    val progress = if (safeDurationMillis == 0) 0f else elapsedForProgress.toFloat() / safeDurationMillis.toFloat()
    val borderColor =
        when (state) {
            is IntervalTimerState.Completed -> AppColor.surface
            is IntervalTimerState.Paused -> AppColor.orange
            IntervalTimerState.Pending -> AppColor.surface
            is IntervalTimerState.Started -> AppColor.primary
        }
    val progressFillColor = borderColor.copy(alpha = 0.1f)
    val timerString =
        when (state) {
            is IntervalTimerState.Started -> TimeFormatter.formatIntervalTime(elapsedForProgress)
            else -> TimeFormatter.formatIntervalTime(totalSeconds)
        }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = AppColor.surface),
    ) {
        val badgeColor =
            when (state) {
                is IntervalTimerState.Completed -> AppColor.textTertiary
                is IntervalTimerState.Paused -> AppColor.orange
                IntervalTimerState.Pending -> AppColor.textSecondary
                is IntervalTimerState.Started -> AppColor.primary
            }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .drawBehind {
                        if (progress > 0f) {
                            drawRect(
                                color = progressFillColor,
                                size = size.copy(width = size.width * progress),
                            )
                        }
                    },
        ) {
            Row(
                modifier =
                    Modifier.padding(
                        horizontal = AppSpacing.m,
                        vertical = AppSpacing.s,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NumberBadge(
                    value = index.toString(),
                    color = badgeColor,
                )
                Text(
                    modifier =
                        Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 12.dp,
                        ),
                    style = AppTypography.label,
                    color = AppColor.textPrimary,
                    text = title,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    style = AppTypography.mono,
                    color = badgeColor,
                    text = timerString,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
private fun IntervalItemPreview() {
    ThemePreviewContainer {
        IntervalItem(
            modifier = Modifier.fillMaxWidth(),
            index = 3,
            title = "Title",
            totalSeconds = 60,
            elapsedSeconds = 0,
            state = IntervalTimerState.Pending,
        )
    }
}

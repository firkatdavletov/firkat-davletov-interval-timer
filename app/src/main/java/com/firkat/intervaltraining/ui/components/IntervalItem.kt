package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firkat.intervaltraining.R
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
    val safeDurationSec = totalSeconds.coerceAtLeast(0)
    val safeElapsedSec = elapsedSeconds.coerceAtLeast(0)
    val elapsedForProgress = if (safeDurationSec == 0) 0 else safeElapsedSec.coerceAtMost(safeDurationSec)
    val progress = if (safeDurationSec == 0) 0f else elapsedForProgress.toFloat() / safeDurationSec.toFloat()
    val borderColor =
        when (state) {
            is IntervalTimerState.Completed -> AppColor.disabledBg
            is IntervalTimerState.Paused -> AppColor.orange
            IntervalTimerState.Pending -> AppColor.surface
            IntervalTimerState.Selected -> AppColor.primary
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
        colors = CardDefaults.cardColors(
            containerColor = if (state is IntervalTimerState.Completed) {
                AppColor.disabledBg
            } else {
                AppColor.surface
            }
        ),
    ) {
        val badgeColor =
            when (state) {
                is IntervalTimerState.Completed -> AppColor.disabledBg
                is IntervalTimerState.Paused -> AppColor.orange
                IntervalTimerState.Pending -> AppColor.disabledBg
                IntervalTimerState.Selected -> AppColor.primary
                is IntervalTimerState.Started -> AppColor.primary
            }
        val badgeContentColor =
            when (state) {
                is IntervalTimerState.Completed -> AppColor.textTertiary
                is IntervalTimerState.Paused -> AppColor.surface
                IntervalTimerState.Pending -> AppColor.textTertiary
                IntervalTimerState.Selected -> AppColor.surface
                is IntervalTimerState.Started -> AppColor.surface
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
                    Modifier.padding(AppSpacing.m),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state is IntervalTimerState.Completed) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(AppColor.textTertiary)
                    )
                } else {
                    NumberBadge(
                        value = index.toString(),
                        containerColor = badgeColor,
                        contentColor =badgeContentColor
                    )
                }

                Text(
                    modifier =
                        Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 12.dp,
                        ),
                    style = AppTypography.label,
                    color = if (state is IntervalTimerState.Completed) {
                        AppColor.textTertiary
                    } else {
                        AppColor.textPrimary
                    },
                    text = title,
                    textDecoration = if (state is IntervalTimerState.Completed) {
                        TextDecoration.LineThrough
                    } else null
                )
                Spacer(Modifier.weight(1f))
                Text(
                    style = AppTypography.mono,
                    color = if (state is IntervalTimerState.Completed) {
                        AppColor.textTertiary
                    } else {
                        AppColor.textSecondary
                    },
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
            title = stringResource(R.string.preview_interval_title),
            totalSeconds = 60,
            elapsedSeconds = 0,
            state = IntervalTimerState.Completed,
        )
    }
}

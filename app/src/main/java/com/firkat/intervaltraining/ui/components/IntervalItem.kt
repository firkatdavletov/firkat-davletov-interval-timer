package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.AppTypography

sealed interface IntervalItemState {
    data object Pending : IntervalItemState

    data object Processing : IntervalItemState

    data object InPause : IntervalItemState

    data object Complete : IntervalItemState
}

@Composable
fun IntervalItem(
    modifier: Modifier = Modifier,
    index: Int,
    title: String,
    subtitle: String,
    timerValue: String,
    state: IntervalItemState,
    progress: Float? = null,
) {
    val progressFraction = (progress ?: 0f).coerceIn(0f, 1f)
    val borderColor =
        when (state) {
            IntervalItemState.Complete -> AppColor.surface
            IntervalItemState.InPause -> AppColor.orange
            IntervalItemState.Pending -> AppColor.surface
            IntervalItemState.Processing -> AppColor.primary
        }
    val progressFillColor = borderColor.copy(alpha = 0.1f)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = AppColor.surface),
    ) {
        val badgeColor =
            when (state) {
                IntervalItemState.Complete -> AppColor.textTertiary
                IntervalItemState.InPause -> AppColor.orange
                IntervalItemState.Pending -> AppColor.textSecondary
                IntervalItemState.Processing -> AppColor.primary
            }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .drawBehind {
                        if (progressFraction > 0f) {
                            drawRect(
                                color = progressFillColor,
                                size = size.copy(width = size.width * progressFraction),
                            )
                        }
                    },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = AppSpacing.m),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NumberBadge(
                    value = index.toString(),
                    color = badgeColor,
                )
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    Text(
                        style = AppTypography.label,
                        color = AppColor.textPrimary,
                        text = title,
                    )
                    Text(
                        style = AppTypography.body,
                        color = AppColor.textSecondary,
                        text = subtitle,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    style = AppTypography.mono,
                    color = badgeColor,
                    text = timerValue,
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
            subtitle = "Subtitle",
            timerValue = "00:30",
            state = IntervalItemState.Processing,
            progress = 0.6f,
        )
    }
}

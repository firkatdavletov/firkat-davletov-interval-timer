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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.AppTypography
import com.firkat.intervaltraining.util.TimeFormatter
import java.util.Locale

@Composable
fun TimerCard(
    modifier: Modifier = Modifier,
    title: String,
    stateLabel: String,
    elapsedMillis: Long,
    durationMillis: Long,
    accentColor: Color = AppColor.primary,
) {
    val safeDurationMillis = durationMillis.coerceAtLeast(0L)
    val safeElapsedMillis = elapsedMillis.coerceAtLeast(0L)
    val elapsedForProgress = if (safeDurationMillis == 0L) 0L else safeElapsedMillis.coerceAtMost(safeDurationMillis)
    val progress = if (safeDurationMillis == 0L) 0f else elapsedForProgress.toFloat() / safeDurationMillis.toFloat()

    val formattedElapsed = TimeFormatter.formatIntervalTime(safeElapsedMillis)
    val formattedDuration = TimeFormatter.formatIntervalTime(safeDurationMillis)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, AppColor.border),
        colors = CardDefaults.cardColors(containerColor = AppColor.surface),
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.s),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stateLabel.uppercase(Locale.ROOT),
                style = AppTypography.state,
                color = accentColor
            )
            Text(
                text = title,
                style = AppTypography.label,
                color = AppColor.textSecondary
            )
            Text(
                text = formattedElapsed,
                style = AppTypography.timerDisplay,
                color = accentColor
            )
            Text(
                text = "Прошло $formattedElapsed из $formattedDuration",
                style = AppTypography.caption,
                color = AppColor.textTertiary
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AppColor.border)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(accentColor)
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
            stateLabel = "Выполняется",
            elapsedMillis = 30_000L,
            durationMillis = 5 * 60_000L,
        )
    }
}

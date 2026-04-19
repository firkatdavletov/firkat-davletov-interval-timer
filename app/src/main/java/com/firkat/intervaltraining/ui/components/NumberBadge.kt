package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppTypography

@Composable
fun NumberBadge(
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(28.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value,
            style = AppTypography.caption,
            color = AppColor.surface,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
private fun NumberBadgePreview() {
    ThemePreviewContainer {
        NumberBadge(
            value = "3",
            color = AppColor.primary,
        )
    }
}

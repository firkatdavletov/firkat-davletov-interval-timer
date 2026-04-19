package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.AppTypography

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    negative: Boolean = false,
) {
    val contentColor = if (negative) AppColor.error else AppColor.textSecondary
    val borderColor = if (negative) AppColor.error.copy(alpha = 0.1f) else AppColor.border

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, borderColor),
        modifier = modifier.height(44.dp),
        contentPadding = PaddingValues(horizontal = AppSpacing.l),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = AppColor.surface,
                contentColor = contentColor,
                disabledContentColor = AppColor.disabledText,
            ),
    ) {
        Text(
            text = text,
            style = AppTypography.button,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
private fun GhostButtonPreview() {
    ThemePreviewContainer {
        GhostButton(
            text = "Новая тренировка",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

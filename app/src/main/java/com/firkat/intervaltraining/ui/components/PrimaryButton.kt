package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color = AppColor.primary,
    content: @Composable (RowScope.() -> Unit)
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(52.dp),
        contentPadding = PaddingValues(horizontal = AppSpacing.l),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = AppColor.surface,
                disabledContainerColor = accentColor.copy(alpha = 0.1f),
                disabledContentColor = AppColor.primary.copy(alpha = 0.5f),
            ),
        content = content,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
private fun PrimaryButtonPreview() {
    ThemePreviewContainer {
        PrimaryButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            content = {
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
            }
        )
    }
}

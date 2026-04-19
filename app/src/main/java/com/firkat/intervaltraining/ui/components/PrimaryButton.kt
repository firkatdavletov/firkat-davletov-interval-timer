package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.AppTypography

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(52.dp),
        contentPadding = PaddingValues(horizontal = AppSpacing.l),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = AppColor.primary,
                contentColor = AppColor.surface,
                disabledContainerColor = AppColor.disabledBg,
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
private fun PrimaryButtonPreview() {
    ThemePreviewContainer {
        PrimaryButton(
            text = "Start Workout",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firkat.intervaltraining.R
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.AppTypography

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    errorText: String? = null,
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.s),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            isError = errorText != null,
            singleLine = true,
            shape = shape,
            modifier =
                Modifier
                    .height(52.dp)
                    .fillMaxWidth(),
            textStyle = AppTypography.body.copy(color = AppColor.textPrimary),
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = AppTypography.body,
                        color = AppColor.textTertiary,
                    )
                }
            },
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColor.border,
                    unfocusedBorderColor = AppColor.border,
                    disabledBorderColor = AppColor.border,
                    focusedContainerColor = AppColor.surface,
                    unfocusedContainerColor = AppColor.surface,
                    disabledContainerColor = AppColor.disabledBg,
                    focusedTextColor = AppColor.textPrimary,
                    unfocusedTextColor = AppColor.textPrimary,
                    disabledTextColor = AppColor.disabledText,
                    cursorColor = AppColor.primary,
                    errorBorderColor = AppColor.error,
                    errorTextColor = AppColor.error,
                ),
        )
        if (errorText != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_error),
                    contentDescription = null,
                    tint = AppColor.error,
                )
                Text(
                    text = errorText,
                    color = AppColor.error,
                    style = AppTypography.caption,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
private fun InputFieldPreview() {
    var text by remember { mutableStateOf("") }
    ThemePreviewContainer {
        InputField(
            value = text,
            onValueChange = { text = it },
            placeholder = "Workout name",
            modifier = Modifier.fillMaxWidth(),
            errorText = "Тренировка не найденв. Проверьте ID",
        )
    }
}

package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firkat.intervaltraining.R
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        colors =
            IconButtonDefaults.iconButtonColors(
                containerColor = AppColor.surface,
            ),
    ) {
        Image(
            modifier =
                Modifier
                    .border(width = 1.dp, color = AppColor.border, shape = CircleShape)
                    .padding(AppSpacing.s),
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AppColor.textTertiary),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
private fun BackButtonPreview() {
    ThemePreviewContainer {
        BackButton(onClick = {})
    }
}

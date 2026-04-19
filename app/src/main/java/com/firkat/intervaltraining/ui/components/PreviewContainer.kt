package com.firkat.intervaltraining.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.firkat.intervaltraining.ui.theme.AppColor
import com.firkat.intervaltraining.ui.theme.AppSpacing
import com.firkat.intervaltraining.ui.theme.IntervalTrainingTheme

@Composable
fun PreviewContainer(content: @Composable ColumnScope.() -> Unit) {
    IntervalTrainingTheme(dynamicColor = false) {
        Surface(color = AppColor.bg) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.xxl),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.m),
                content = content,
            )
        }
    }
}

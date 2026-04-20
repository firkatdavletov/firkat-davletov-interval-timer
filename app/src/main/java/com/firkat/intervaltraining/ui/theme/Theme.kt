package com.firkat.intervaltraining.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun IntervalTrainingTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        typography = Typography,
        content = content,
    )
}

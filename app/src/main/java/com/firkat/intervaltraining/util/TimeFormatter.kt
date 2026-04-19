package com.firkat.intervaltraining.util

import java.util.Locale

object TimeFormatter {
    fun formatIntervalTime(timeMillis: Long): String {
        val totalSeconds = (timeMillis.coerceAtLeast(0L) / 1_000L)
        val hours = totalSeconds / 3_600L
        val minutes = (totalSeconds % 3_600L) / 60L
        val seconds = totalSeconds % 60L

        return if (hours > 0L) {
            String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
        }
    }

    fun formatIntervalTime(timeSeconds: Int): String {
        val hours = timeSeconds / 3_600L
        val minutes = (timeSeconds % 3_600L) / 60L
        val seconds = timeSeconds % 60L

        return if (hours > 0L) {
            String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
        }
    }
}
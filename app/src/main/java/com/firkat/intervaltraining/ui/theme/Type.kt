package com.firkat.intervaltraining.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.firkat.intervaltraining.R

private val Roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_semibold, FontWeight.SemiBold),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

private val RobotoMono = FontFamily(
    Font(R.font.robotomono_semibold, FontWeight.SemiBold),
    Font(R.font.robotomono_bold, FontWeight.Bold)
)

object AppTypography {
    val timerDisplay = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Bold,
        fontSize = 68.sp,
        lineHeight = 68.sp
    )

    val h1 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.5.sp
    )

    val title = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.8.sp
    )

    val body = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.5.sp
    )

    val label = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    )

    val caption = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.2.sp
    )

    val state = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 11.sp,
        letterSpacing = 1.5.sp
    )

    val button = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 15.sp
    )

    val mono = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    )
}

val Typography = Typography(
    displayLarge = AppTypography.timerDisplay,
    headlineLarge = AppTypography.h1,
    titleMedium = AppTypography.title,
    titleSmall = AppTypography.button,
    bodyLarge = AppTypography.body,
    labelLarge = AppTypography.label,
    labelMedium = AppTypography.caption,
    labelSmall = AppTypography.state
)

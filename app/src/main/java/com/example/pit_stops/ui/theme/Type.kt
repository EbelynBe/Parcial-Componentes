package com.example.pit_stops.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pit_stops.R

// Set of Material typography styles to start with
val vipnagorgiallaFamily = FontFamily(
    Font(R.font.vipnagorgialla_bd, FontWeight.Bold),
    Font(R.font.vipnagorgialla_bd_it, FontWeight.ExtraBold),
    Font(R.font.vipnagorgialla_rg, FontWeight.Light),
    Font(R.font.vipnagorgialla_rg_it, FontWeight.ExtraLight),
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = vipnagorgiallaFamily,
        fontWeight = FontWeight.Light,
        fontSize = 13.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = vipnagorgiallaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )
/* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
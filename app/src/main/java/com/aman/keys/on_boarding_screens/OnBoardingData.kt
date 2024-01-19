package com.aman.keys.on_boarding_screens

import androidx.compose.ui.graphics.Color
import com.aman.keys.ui.theme.ColorBlue

data class OnBoardingData(
    val image: Int, val title: String,
    val desc: String,
    val backgroundColor:Color,
    val mainColor:Color = ColorBlue
)
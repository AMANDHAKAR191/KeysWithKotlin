package com.aman.keyswithkotlin.on_boarding_screens

import androidx.compose.ui.graphics.Color
import com.aman.keyswithkotlin.ui.theme.ColorBlue

data class OnBoardingData(
    val image: Int, val title: String,
    val desc: String,
    val backgroundColor:Color,
    val mainColor:Color = ColorBlue
)
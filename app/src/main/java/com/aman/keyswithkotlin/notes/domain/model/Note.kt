package com.aman.keyswithkotlin.notes.domain.model

import androidx.compose.ui.graphics.toArgb
import com.aman.keyswithkotlin.ui.theme.BabyBlue
import com.aman.keyswithkotlin.ui.theme.LightGreen
import com.aman.keyswithkotlin.ui.theme.RedOrange
import com.aman.keyswithkotlin.ui.theme.RedPink
import com.aman.keyswithkotlin.ui.theme.Violet

data class Note constructor(
    val date: String = "",
    var noteTitle: String = "",
    var noteBody: String = "",
    val timestamp: String = "",
    val color: Int = noteColors.random().toArgb(),
) {
    companion object {
        val noteColors = listOf(RedOrange, LightGreen, Violet, BabyBlue, RedPink)
    }
}

class InvalidNoteException(message: String) : Exception(message)

package com.aman.keys.notes.domain.model

import androidx.compose.ui.graphics.toArgb
import com.aman.keys.ui.theme.BabyBlue
import com.aman.keys.ui.theme.LightGreen
import com.aman.keys.ui.theme.RedOrange
import com.aman.keys.ui.theme.RedPink
import com.aman.keys.ui.theme.Violet
import okhttp3.internal.toHexString

data class Note constructor(
    var noteBody: String = "",
    val timestamp: String = "",
    val color: String = "#" + noteColors.random().toArgb().toHexString(),
) {
    companion object {
        val noteColors = listOf(RedOrange, LightGreen, Violet, BabyBlue, RedPink)
    }
}

class InvalidNoteException(message: String) : Exception(message)

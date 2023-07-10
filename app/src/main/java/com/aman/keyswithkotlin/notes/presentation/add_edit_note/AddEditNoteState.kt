package com.aman.keyswithkotlin.notes.presentation.add_edit_note

import androidx.compose.ui.graphics.toArgb
import com.aman.keyswithkotlin.notes.domain.model.Note
import okhttp3.internal.toHexString

data class AddEditNoteState(
    val date: String = "",
    var noteTitle: String = "",
    var noteBody: String = "",
    val timestamp: String = "",
    val color: String = "#" + Note.noteColors.random().toArgb().toHexString()
)

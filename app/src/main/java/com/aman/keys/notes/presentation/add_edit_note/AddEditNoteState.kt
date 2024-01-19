package com.aman.keys.notes.presentation.add_edit_note

import androidx.compose.ui.graphics.toArgb
import com.aman.keys.notes.domain.model.Note
import okhttp3.internal.toHexString

data class AddEditNoteState(
    val date: String = "",
    var noteBody: String = "",
    val timestamp: String = "",
    val color: String = "#" + Note.noteColors.random().toArgb().toHexString()
)

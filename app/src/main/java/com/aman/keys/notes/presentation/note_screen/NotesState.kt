package com.aman.keys.notes.presentation.note_screen

import com.aman.keys.notes.domain.model.Note

data class NotesState(
    val notes: List<Note> = emptyList(),
    val error: String? = "",
    val isLoading: Boolean = false
)

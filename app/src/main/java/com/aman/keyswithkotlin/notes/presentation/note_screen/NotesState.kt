package com.aman.keyswithkotlin.notes.presentation.note_screen

import com.aman.keyswithkotlin.notes.domain.model.Note

data class NotesState(
    val notes: List<Note> = emptyList(),
    val error: String? = "",
    val isLoading: Boolean = false
)

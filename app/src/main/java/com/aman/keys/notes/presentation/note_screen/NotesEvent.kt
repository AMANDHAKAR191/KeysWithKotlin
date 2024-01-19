package com.aman.keys.notes.presentation.note_screen

import com.aman.keys.notes.domain.model.Note

sealed class NotesEvent {
    data class DeleteNote(val note: Note) : NotesEvent()
    object RestoreNote : NotesEvent()
}

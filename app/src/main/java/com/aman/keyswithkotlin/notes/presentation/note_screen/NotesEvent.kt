package com.aman.keyswithkotlin.notes.presentation.note_screen

import com.aman.keyswithkotlin.notes.domain.model.Note

sealed class NotesEvent {
    data class DeleteNote(val note: Note) : NotesEvent()
    object RestoreNote : NotesEvent()
}

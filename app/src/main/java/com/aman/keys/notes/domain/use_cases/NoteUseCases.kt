package com.aman.keys.notes.domain.use_cases

data class NoteUseCases(
    val addNote: AddNote,
    val deleteNote: DeleteNote,
    val getNotes: GetNotes,
    val shareNote: ShareNote
)
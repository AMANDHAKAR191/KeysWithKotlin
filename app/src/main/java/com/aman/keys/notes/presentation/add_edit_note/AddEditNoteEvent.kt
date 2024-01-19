package com.aman.keys.notes.presentation.add_edit_note

sealed class AddEditNoteEvent {
    data class EnteredContent(val value: String) : AddEditNoteEvent()
    data class ChangeColor(val color: String) : AddEditNoteEvent()
    object SaveNote : AddEditNoteEvent()
}


package com.aman.keyswithkotlin.notes.presentation.add_edit_note

import com.aman.keyswithkotlin.passwords.domain.model.Password

sealed class NoteEvent {
    data class EnteredNoteTitle(val value:String): NoteEvent()
    data class EnteredNoteBody(val value:String): NoteEvent()

    object SavePassword: NoteEvent()
    data class RestorePassword(val password: Password) : NoteEvent()
    data class DeletePassword(val password: Password) : NoteEvent()
}
package com.aman.keyswithkotlin.notes.domain.use_cases


import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.notes.domain.model.InvalidNoteException
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import com.aman.keyswithkotlin.passwords.domain.model.InvalidPasswordException
import kotlinx.coroutines.flow.Flow

class AddNote(
    private val noteRepository: NoteRepository
) {
    @Throws(InvalidPasswordException::class)
    operator fun invoke(note: Note): Flow<Response<Pair<String?, Boolean?>>> {
        if (note.noteTitle.isBlank()) {
            throw InvalidNoteException("The noteTitle can't be empty.")
        }
        if (note.noteBody.isBlank()) {
            throw InvalidNoteException("The noteBody can't be empty.")
        }
        println("check1: $noteRepository")
        return noteRepository.insertNote(note)
    }
}
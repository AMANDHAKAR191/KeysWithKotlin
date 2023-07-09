package com.aman.keyswithkotlin.notes.domain.use_cases


import android.provider.ContactsContract
import com.aman.keyswithkotlin.passwords.domain.model.InvalidPasswordException
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.Throws

class AddNote(
    private val noteRepository: NoteRepository
) {
    @Throws(InvalidPasswordException::class)
    suspend operator fun invoke(note: Note): Flow<Response<Pair<String?, Boolean?>>> {
        if (note.noteTitle.isBlank()){
            throw InvalidPasswordException("The noteTitle can't be empty.")
        }
        if (note.noteBody.isBlank()){
            throw InvalidPasswordException("The noteBody can't be empty.")
        }
        return noteRepository.insertNote(note)
    }
}
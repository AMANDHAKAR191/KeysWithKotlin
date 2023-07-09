package com.aman.keyswithkotlin.notes.domain.use_cases

import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class DeleteNote(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note:Note): Flow<Response<Pair<String?, Boolean?>>> {
        return noteRepository.deleteNote(note)
    }
}
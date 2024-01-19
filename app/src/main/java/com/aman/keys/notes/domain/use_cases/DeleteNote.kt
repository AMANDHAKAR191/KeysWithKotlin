package com.aman.keys.notes.domain.use_cases

import com.aman.keys.core.util.Response
import com.aman.keys.notes.domain.model.Note
import com.aman.keys.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class DeleteNote(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note:Note): Flow<Response<Pair<String?, Boolean?>>> {
        return noteRepository.deleteNote(note)
    }
}
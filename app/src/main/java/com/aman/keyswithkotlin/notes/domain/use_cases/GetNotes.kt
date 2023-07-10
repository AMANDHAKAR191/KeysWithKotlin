package com.aman.keyswithkotlin.notes.domain.use_cases

import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotes (
    private val noteRepository: NoteRepository
) {
    operator fun invoke():Flow<Response<Pair<MutableList<Note>?, Boolean?>>>{
        return noteRepository.getNotes()
    }
}
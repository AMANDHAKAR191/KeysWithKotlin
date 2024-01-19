package com.aman.keys.notes.domain.repository

import com.aman.keys.core.util.Response
import com.aman.keys.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotes(): Flow<Response<Pair<MutableList<Note>?, Boolean?>>>

    fun insertNote(note: Note): Flow<Response<Pair<String?, Boolean?>>>

    fun deleteNote(note: Note): Flow<Response<Pair<String?, Boolean?>>>
}
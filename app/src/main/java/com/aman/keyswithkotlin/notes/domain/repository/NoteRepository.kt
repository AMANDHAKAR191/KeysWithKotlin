package com.aman.keyswithkotlin.notes.domain.repository

import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.passwords.domain.model.Password
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotes(): Flow<Response<Pair<MutableList<Note>?, Boolean?>>>

    fun insertNote(note: Note): Flow<Response<Pair<String?, Boolean?>>>

    fun deleteNote(note: Note): Flow<Response<Pair<String?, Boolean?>>>
}
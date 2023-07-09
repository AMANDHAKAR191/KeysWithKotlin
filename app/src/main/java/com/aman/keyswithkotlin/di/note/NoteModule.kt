package com.aman.keyswithkotlin.di.note

import com.aman.keyswithkotlin.di.AESKeySpacs
import com.aman.keyswithkotlin.notes.data.repository.NoteRepositoryImpl
import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import com.aman.keyswithkotlin.notes.domain.use_cases.AddNote
import com.aman.keyswithkotlin.notes.domain.use_cases.DeleteNote
import com.aman.keyswithkotlin.notes.domain.use_cases.GetNotes
import com.aman.keyswithkotlin.notes.domain.use_cases.NoteUseCases
import com.aman.keyswithkotlin.notes.domain.use_cases.ShareNote
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class NoteModule {


    @Provides
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            addNote = AddNote(repository),
            deleteNote = DeleteNote(repository),
            shareNote = ShareNote(repository)
        )
    }

    @Provides
    fun provideNoteRepository(
        database: FirebaseDatabase,
        UID: String,
        aesKeySpacs: AESKeySpacs
    ): NoteRepository {
        return NoteRepositoryImpl(database, UID = UID, aesKeySpacs)
    }
}
package com.aman.keys.di.note

import com.aman.keys.di.AESKeySpecs
import com.aman.keys.di.AES_CLOUD_KEY_SPECS
import com.aman.keys.di.AES_LOCAL_KEY_SPECS
import com.aman.keys.di.UID
import com.aman.keys.notes.data.repository.NoteRepositoryImpl
import com.aman.keys.notes.domain.repository.NoteRepository
import com.aman.keys.notes.domain.use_cases.AddNote
import com.aman.keys.notes.domain.use_cases.DeleteNote
import com.aman.keys.notes.domain.use_cases.GetNotes
import com.aman.keys.notes.domain.use_cases.NoteUseCases
import com.aman.keys.notes.domain.use_cases.ShareNote
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class NoteModule {


    @Provides
    fun provideNoteUseCases(
        repository: NoteRepository,
        @AES_CLOUD_KEY_SPECS
        aesCloudKeySpecs: AESKeySpecs,
        @AES_LOCAL_KEY_SPECS
        aesLocalKeySpecs: AESKeySpecs,
    ): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository, aesCloudKeySpecs,aesLocalKeySpecs),
            addNote = AddNote(repository, aesCloudKeySpecs,aesLocalKeySpecs),
            deleteNote = DeleteNote(repository),
            shareNote = ShareNote(repository)
        )
    }

    @Provides
    fun provideNoteRepository(
        database: FirebaseDatabase,
        @UID
        UID: String,
        @AES_CLOUD_KEY_SPECS
        aesCloudKeySpecs: AESKeySpecs,
        @AES_LOCAL_KEY_SPECS
        aesLocalKeySpecs: AESKeySpecs,
    ): NoteRepository {
        return NoteRepositoryImpl(database, UID = UID, aesCloudKeySpecs)
    }
}
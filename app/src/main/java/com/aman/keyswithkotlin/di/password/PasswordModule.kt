package com.aman.keyswithkotlin.di.password

import com.aman.keyswithkotlin.passwords.data.repository.PasswordRepositoryImpl
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import com.aman.keyswithkotlin.passwords.domain.use_cases.AddPassword
import com.aman.keyswithkotlin.passwords.domain.use_cases.DeletePassword
import com.aman.keyswithkotlin.passwords.domain.use_cases.GetPasswords
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class PasswordModule {

    @Provides
    fun provideFirebaseDatabase() = Firebase.database

    @Provides
    fun providePasswordUseCases(repository: PasswordRepository): PasswordUseCases {
        return PasswordUseCases(
            getPasswords = GetPasswords(repository),
            addPassword = AddPassword(repository),
            deletePassword = DeletePassword(repository)
        )
    }
    @Provides
    fun providePasswordRepository(
        database: FirebaseDatabase
    ): PasswordRepository {
        return PasswordRepositoryImpl(database)
    }
}
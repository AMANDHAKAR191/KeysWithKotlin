package com.aman.keyswithkotlin.di.password

import com.aman.keyswithkotlin.di.AESKeySpacs
import com.aman.keyswithkotlin.passwords.data.repository.PasswordRepositoryImpl
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import com.aman.keyswithkotlin.passwords.domain.use_cases.AddPassword
import com.aman.keyswithkotlin.passwords.domain.use_cases.DeletePassword
import com.aman.keyswithkotlin.passwords.domain.use_cases.GeneratePassword
import com.aman.keyswithkotlin.passwords.domain.use_cases.GetPasswords
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class PasswordModule {
    @Provides
    fun providePasswordUseCases(
        repository: PasswordRepository,
        aesKeySpacs: AESKeySpacs
    ): PasswordUseCases {
        return PasswordUseCases(
            getPasswords = GetPasswords(repository, aesKeySpacs),
            addPassword = AddPassword(repository, aesKeySpacs),
            deletePassword = DeletePassword(repository),
            generatePassword = GeneratePassword(repository)
        )
    }

    @Provides
    fun providePasswordRepository(
        database: FirebaseDatabase,
        UID: String
    ): PasswordRepository {
        return PasswordRepositoryImpl(database, UID = UID)
    }
}
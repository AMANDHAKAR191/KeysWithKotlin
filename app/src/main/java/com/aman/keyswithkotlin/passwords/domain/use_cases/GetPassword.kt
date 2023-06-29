package com.aman.keyswithkotlin.passwords.domain.use_cases

import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository

class GetPassword(
    private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke(id: Int): Password? {
        return passwordRepository.getPasswordById(id)
    }
}
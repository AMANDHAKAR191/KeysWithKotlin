package com.aman.keyswithkotlin.notes.domain.use_cases

import com.aman.keyswithkotlin.core.AES
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.di.AESKeySpecs
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNotes(
    private val noteRepository: NoteRepository,
    private val aesKeySpecs: AESKeySpecs
) {
    operator fun invoke(): Flow<Response<Pair<MutableList<Note>?, Boolean?>>> {
        val aes = AES.getInstance(aesKeySpecs.aesKey, aesKeySpecs.aesIV)
            ?: throw IllegalStateException("Failed to initialize AES instance.")
        return noteRepository.getNotes()
            .map { response ->
                when (response) {
                    is Response.Success -> {
                        println("response: ${response.data}")
                        val decryptedPasswords = response.data?.map { encryptedPassword ->
                            if (encryptedPassword.noteTitle.isBlank()) {
                                encryptedPassword
                            } else {
                                decryptNote(encryptedPassword, aes)
                            }
                        }
                        response.copy(data = decryptedPasswords?.sortedByDescending { it.timestamp }?.toMutableList() as MutableList<Note>?)
                    }

                    else -> response
                }
            }
    }

    private fun decryptNote(encryptedNote: Note, aes: AES): Note {
        val note = encryptedNote.copy()
        note.noteTitle = aes.singleDecryption(encryptedNote.noteTitle)
        note.noteBody = aes.singleDecryption(encryptedNote.noteBody)
        // Decrypt any other user properties as needed

        return note
    }
}
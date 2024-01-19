package com.aman.keys.notes.domain.use_cases


import com.aman.keys.core.AES
import com.aman.keys.core.util.Response
import com.aman.keys.di.AESKeySpecs
import com.aman.keys.notes.domain.model.InvalidNoteException
import com.aman.keys.notes.domain.model.Note
import com.aman.keys.notes.domain.repository.NoteRepository
import com.aman.keys.passwords.domain.model.InvalidPasswordException
import kotlinx.coroutines.flow.Flow

class AddNote(
    private val noteRepository: NoteRepository,
    private val aesCloudKeySpecs: AESKeySpecs,
    private val aesLocalKeySpecs: AESKeySpecs
) {
    @Throws(InvalidPasswordException::class)
    operator fun invoke(note: Note): Flow<Response<Pair<String?, Boolean?>>> {
        if (note.noteBody.isBlank()) {
            throw InvalidNoteException("The noteBody can't be empty.")
        }
        println("check1: $noteRepository")
        val aes = AES.getInstance(aesCloudKeySpecs.aesKey, aesCloudKeySpecs.aesIV)
            ?: throw IllegalStateException("Failed to initialize AES instance.")

        val encryptedNote = encryptNote(note, aes)
        return noteRepository.insertNote(encryptedNote)
    }

    private fun encryptNote(note: Note, aes: AES): Note {
        val encryptedNote = note.copy()
        encryptedNote.noteBody = aes.singleEncryption(note.noteBody)
        // Encrypt any other user properties as needed

        return encryptedNote
    }
}
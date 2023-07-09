package com.aman.keyswithkotlin.notes.data.repository

import com.aman.keyswithkotlin.core.AES
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.di.AESKeySpacs
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NoteRepositoryImpl(
    private val database: FirebaseDatabase,
    private val UID: String,
    private val aesKeySpacs: AESKeySpacs
) : NoteRepository {
    private val _notesItemsTemp = mutableListOf<Note>()
    private val _notesItems = mutableListOf<Note>()
    var aesKEY: String = ""
    var aesIV = ""

    override fun getNotes(): Flow<Response<Pair<MutableList<Note>?, Boolean?>>> =
        callbackFlow {
            println("aesKEY: $aesKEY || aesIV: $aesIV")
            var isNoteRetrieved = false
            val reference = database.reference.child("Notes").child(UID)
            reference.keepSynced(true)
            trySend(Response.Loading)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    _notesItemsTemp.clear()
                    println("dataSnapshot: $dataSnapshot")
                    for (ds in dataSnapshot.children) {
                        val items = ds.getValue(Note::class.java)
                        if (items != null) {
                            _notesItemsTemp.add(items)
                        }
                    }
                    isNoteRetrieved = true
                    println("notes: $_notesItemsTemp")
                    trySend(Response.Success(data = _notesItemsTemp))

                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Failure(error.toException()))
                }
            }
            //todo you can't put this method inside the onDataChange so find another method
            println("isNoteRetrieved: $isNoteRetrieved")
            if (isNoteRetrieved) {
                //adjust this code when this run that time _passwordsItems list is empty
                _notesItemsTemp.forEach { note ->
                    AES.getInstance(aesKey = aesKEY, aes_Iv = aesIV)?.let { aes ->
                        val decryptedPassword = decryptPassword(note, aes)
                        _notesItems.add(decryptedPassword)
                    }
                }
                println("notes1: $_notesItems")
                trySend(Response.Success(data = _notesItems))
            }
            reference.addValueEventListener(listener)
            awaitClose {
                reference.removeEventListener(listener)
                close()
            }
        }

    override fun insertNote(note: Note): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {

            val reference = database.reference.child("Notes").child(UID)
            reference.keepSynced(true)
            trySend(Response.Loading)
            val _note = Note(
                date = note.date,
                noteTitle = note.noteTitle,
                noteBody = note.noteBody
            )

            AES.getInstance(aesKeySpacs.aesKey, aesKeySpacs.aesIV)?.let { aes ->
                val encryptedPassword = encryptPassword(_note, aes)
                try {
                    reference.child(note.noteTitle)
                        .setValue(encryptedPassword)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                trySend(Response.Success("Note is successfully saved"))
                            }
                        }
                        .addOnFailureListener {
                            trySend(Response.Failure(it))
                        }
                    awaitClose {
                        close()
                    }
                } catch (e: Exception) {
                    Response.Failure(e)
                }
            } ?: Response.Failure(Exception("AES initialization failed."))

        }

    private fun encryptPassword(note: Note, aes: AES): Note {
        val encryptedNote = note.copy()
        encryptedNote.noteTitle = aes.singleEncryption(note.noteTitle)
        encryptedNote.noteBody = aes.singleEncryption(note.noteBody)
        // Encrypt any other user properties as needed

        return encryptedNote
    }

    private fun decryptPassword(encryptedNote: Note, aes: AES): Note {
        val note = encryptedNote.copy()
        note.noteTitle = aes.singleDecryption(encryptedNote.noteTitle)
        note.noteBody = aes.singleDecryption(encryptedNote.noteBody)
        // Encrypt any other user properties as needed

        return note
    }


    override fun deleteNote(note: Note): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            val reference = database.reference.child("Notes").child(UID)
            reference.keepSynced(true)
            trySend(Response.Loading)
            reference.child(note.noteTitle)
                .removeValue()
                .addOnCompleteListener {
                    trySend(Response.Success(data = "Note is successfully deleted"))
                }
                .addOnFailureListener {
                    trySend(Response.Failure(it))
                }
            awaitClose {
                close()
            }
        }

}
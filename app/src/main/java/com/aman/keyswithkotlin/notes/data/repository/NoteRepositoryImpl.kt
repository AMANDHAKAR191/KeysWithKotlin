package com.aman.keyswithkotlin.notes.data.repository

import com.aman.keyswithkotlin.core.AES
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.di.AESKeySpecs
import com.aman.keyswithkotlin.di.UID
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NoteRepositoryImpl(
    private val database: FirebaseDatabase,
    @UID
    private val UID: String,
    private val aesKeySpecs: AESKeySpecs
) : NoteRepository {
    private val _notesItemsTemp = mutableListOf<Note>()
    private val _notesItems = mutableListOf<Note>()
    var aesKEY: String = ""
    var aesIV: String = ""

    override fun getNotes(): Flow<Response<Pair<MutableList<Note>?, Boolean?>>> =
        callbackFlow {
            println("aesKEY: $aesKEY || aesIV: $aesIV")
            aesKEY = aesKeySpecs.aesKey
            aesIV = aesKeySpecs.aesIV
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
            println("check2")
            println("inside: insertNote $note")
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
            val formatted = current.format(formatter)
            val reference = database.reference.child("Notes").child(UID)
            trySend(Response.Loading)
            if (note.timestamp.isEmpty()) {
                if (isActive) trySend(Response.Failure(Exception("timestamp is empty")))
            }
            val _note = Note(
                noteTitle = note.noteTitle,
                noteBody = note.noteBody,
                timestamp = formatted,
                color = note.color
            )
            println("check3")
            reference.child(_note.timestamp)
                .setValue(_note)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        println("check3")
                        if (isActive) trySend(Response.Success("Note is successfully saved"))
                    }
                }
                .addOnFailureListener {
                    if (isActive) trySend(Response.Failure(it))
                }
            awaitClose {
                close()
            }

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
            trySend(Response.Loading)
            reference.orderByChild("timestamp")
                .equalTo(note.timestamp)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val noteKey = childSnapshot.key
                            println("noteKey: $noteKey")
                            reference.child(noteKey!!)
                                .removeValue()
                                .addOnCompleteListener {
                                    trySend(Response.Success(data = "Note is successfully deleted"))
                                }
                                .addOnFailureListener {
                                    trySend(Response.Failure(it))
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        trySend(Response.Failure(databaseError.toException()))
                    }
                })
            awaitClose {
                close()
            }
        }


}
package com.aman.keys.notes.presentation.note_screen

import UIEvents
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keys.core.util.Response
import com.aman.keys.notes.domain.model.Note
import com.aman.keys.notes.domain.use_cases.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = MutableStateFlow(NotesState())
    val state = _state.asStateFlow()

    private var recentlyDeletedNote: Note? = null

    init {
        getNotes()
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note).collect { response ->
                        withContext(Dispatchers.Main) {
                            when (response) {
                                is Response.Success<*, *> -> {
                                    recentlyDeletedNote = event.note
                                    _eventFlow.emit(
                                        UIEvents.ShowSnackBar(
                                            "Note deleted",
                                            true,
                                            "Restore"
                                        )
                                    )
                                }

                                is Response.Failure -> {
                                    _state.value = state.value.copy(
                                        error = response.e.message ?: "Unexpected error occurred",
                                        isLoading = false
                                    )
                                }

                                is Response.Loading -> {
                                    _state.value = NotesState(
                                        isLoading = true
                                    )
                                }
                            }
                        }

                    }
                }
            }

            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                        .collect {

                        }
                    recentlyDeletedNote = null
                }
            }
        }
    }

    private fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            noteUseCases.getNotes().collect { response ->
                println(this.coroutineContext)
                withContext(Dispatchers.Main) {
                    println(this.coroutineContext)
                    when (response) {
                        is Response.Success<*, *> -> {
                            _state.update {
                                it.copy(
                                    notes = response.data as List<Note>,
                                    isLoading = false
                                )
                            }
                        }

                        is Response.Failure -> {
                            _state.value = state.value.copy(
                                error = response.e.message ?: "Unexpected error occurred",
                                isLoading = false
                            )
                        }

                        is Response.Loading -> {
                            _state.value = NotesState(
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }
}
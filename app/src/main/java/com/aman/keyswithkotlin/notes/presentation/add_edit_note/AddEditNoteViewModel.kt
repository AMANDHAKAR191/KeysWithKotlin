package com.aman.keyswithkotlin.notes.presentation.add_edit_note

import UIEvents
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.notes.domain.model.InvalidNoteException
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.domain.use_cases.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AddEditNoteState())
    val state: State<AddEditNoteState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _state.value = state.value.copy(
                    noteTitle = event.value
                )
            }

            is AddEditNoteEvent.EnteredContent -> {
                _state.value = state.value.copy(
                    noteBody = event.value
                )
            }

            is AddEditNoteEvent.ChangeColor -> {
                _state.value = state.value.copy(
                    color = "#" + event.color
                )
            }

            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNote(
                            Note(
                                noteTitle = state.value.noteTitle,
                                noteBody = state.value.noteBody,
                                timestamp = state.value.timestamp,
                                color = state.value.color,
                            )
                        ).collect { response ->
                            when (response) {
                                is Response.Success<*, *> -> {
                                    _eventFlow.emit(
                                        UIEvents.ShowSnackBar(
                                            message = response.data.toString()
                                        )
                                    )
                                    _eventFlow.emit(
                                        UIEvents.SaveNote
                                    )
                                }

                                is Response.Failure -> {
                                    UIEvents.ShowErrorDialog(response.e.message?:"Unexpected error occurred!")
                                }

                                else -> {}
                            }

                        }
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UIEvents.ShowSnackBar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}
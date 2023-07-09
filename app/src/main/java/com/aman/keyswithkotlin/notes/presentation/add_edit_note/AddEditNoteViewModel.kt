package com.aman.keyswithkotlin.notes.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.domain.use_cases.NoteUseCases
import com.aman.keyswithkotlin.passwords.domain.model.InvalidPasswordException
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordState
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
):ViewModel() {
    private val _eventFlow = MutableSharedFlow<AddEditPasswordViewModel.UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = mutableStateOf(AddEditNoteState())
    val state: State<AddEditNoteState> = _state

    fun onEvent(event: NoteEvent) {
        when (event) {
            is NoteEvent.EnteredNoteTitle -> {
                _state.value = state.value.copy(
                    noteTitle = event.value
                )
            }

            is NoteEvent.EnteredNoteBody -> {
                _state.value = state.value.copy(
                    noteBody = event.value
                )
            }


            is NoteEvent.SavePassword -> {
                viewModelScope.launch {
                    println()
                    try {
                        noteUseCases.addNote(
                            Note(
                                date = "",
                                noteTitle = state.value.noteTitle,
                                noteBody = state.value.noteBody
                            )
                        ).collect{response->
                            when (response) {
                                is Response.Loading -> {

                                }

                                is Response.Success<*, *> -> {
                                    _eventFlow.emit(
                                        AddEditPasswordViewModel.UiEvent.ShowSnackBar(
                                            message = response.data.toString()
                                        )
                                    )
                                }

                                is Response.Failure -> {

                                }
                            }
                            _eventFlow.emit(
                                AddEditPasswordViewModel.UiEvent.savePassword
                            )
                        }

                    } catch (e: InvalidPasswordException) {
                        _eventFlow.emit(
                            AddEditPasswordViewModel.UiEvent.ShowSnackBar(
                                message = e.message ?: "Couldn't save Expense"
                            )
                        )
                    }
                }
            }

            else -> {}
        }
    }


}
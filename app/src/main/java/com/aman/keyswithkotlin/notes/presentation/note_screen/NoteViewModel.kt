package com.aman.keyswithkotlin.notes.presentation.note_screen

import com.aman.keyswithkotlin.notes.domain.use_cases.NoteUseCases
import javax.inject.Inject

class NoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) {

}
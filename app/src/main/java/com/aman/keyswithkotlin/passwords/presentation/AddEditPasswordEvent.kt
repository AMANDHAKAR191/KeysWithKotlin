package com.aman.keyswithkotlin.passwords.presentation

sealed class AddEditPasswordEvent {
    object SavePassword: AddEditPasswordEvent()
}
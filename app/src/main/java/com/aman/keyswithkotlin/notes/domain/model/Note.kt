package com.aman.keyswithkotlin.notes.domain.model

data class Note constructor(
    val date:String = "",
    var noteTitle:String = "",
    var noteBody:String = "",
) {
//    fun doesMatchSearchQuery(query: String): Boolean {
//        val matchingCombination = listOf(
//            "$websiteName",
//            "$userName",
//            "${websiteName.first()}",
//            "${userName.first()}"
//        )
//        return matchingCombination.any {
//            it.contains(query, ignoreCase = true)
//        }
//    }
}

class InvalidNoteException(message: String) : Exception(message)

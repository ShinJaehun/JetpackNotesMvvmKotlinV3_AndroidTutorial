package com.shinjaehun.jetpacknotesmvvmkotlinv3.note.notedetail

sealed class NoteDetailEvent {
    data class OnDoneClick(val contents: String) : NoteDetailEvent()
    object OnDeleteClick : NoteDetailEvent()
    data class OnStart(val noteId: String) : NoteDetailEvent()
}
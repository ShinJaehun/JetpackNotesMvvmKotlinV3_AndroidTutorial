package com.shinjaehun.jetpacknotesmvvmkotlinv3.note.notelist

sealed class NoteListEvent {
    data class OnNoteItemClick(val position: Int): NoteListEvent()
    object OnStart: NoteListEvent()
}
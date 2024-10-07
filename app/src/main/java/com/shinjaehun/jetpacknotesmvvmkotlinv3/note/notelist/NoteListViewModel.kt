package com.shinjaehun.jetpacknotesmvvmkotlinv3.note.notelist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.BaseViewModel
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.GET_NOTES_ERROR
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.Result
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.Note
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.repository.INoteRepository
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val TAG = "NoteListViewModel"

class NoteListViewModel(
    val noteRepo: INoteRepository,
    uiContext: CoroutineContext
) : BaseViewModel<NoteListEvent>(uiContext) {

    private val noteListState = MutableLiveData<List<Note>>()
    val noteList: LiveData<List<Note>> get() = noteListState

    private val editNoteState = MutableLiveData<String>()
    val editNote: LiveData<String> get() = editNoteState

    override fun handleEvent(event: NoteListEvent) {
        when (event) {
            is NoteListEvent.OnStart -> getNotes()
            is NoteListEvent.OnNoteItemClick -> editNote(event.position)
        }
    }

    private fun editNote(position: Int) {
        editNoteState.value = noteList.value!![position].creationDate
    }

    private fun getNotes() = launch {
        Log.i(TAG, "NoteListViewModel getNotes(): ${Thread.currentThread().name}")
        val notesResult = noteRepo.getNotes()

        when (notesResult) {
            is Result.Value -> noteListState.value = notesResult.value
            is Result.Error -> errorState.value = GET_NOTES_ERROR
        }
    }
}
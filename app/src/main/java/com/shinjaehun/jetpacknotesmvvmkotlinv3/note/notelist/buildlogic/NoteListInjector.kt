package com.shinjaehun.jetpacknotesmvvmkotlinv3.note.notelist.buildlogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.RoomNoteDatabase
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.implementations.NoteRepoImpl
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.repository.INoteRepository

class NoteListInjector(application: Application
) : AndroidViewModel(application) {

    private fun getNoteRepository(): INoteRepository {
        FirebaseApp.initializeApp(getApplication())
        return NoteRepoImpl(
            local = RoomNoteDatabase.getInstance(getApplication()).roomNoteDao()
        )
    }

    fun provideNoteListViewModelFactory(): NoteListViewModelFactory =
        NoteListViewModelFactory(getNoteRepository())
}
package com.shinjaehun.jetpacknotesmvvmkotlinv3.model.repository

import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.Result
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.Note

interface INoteRepository {
    suspend fun getNoteById(noteId: String): Result<Exception, Note>
    suspend fun getNotes(): Result<Exception, List<Note>>
    suspend fun deleteNote(note: Note): Result<Exception, Unit>
    suspend fun updateNote(note: Note): Result<Exception, Unit>
}
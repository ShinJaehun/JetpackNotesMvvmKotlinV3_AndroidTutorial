package com.shinjaehun.jetpacknotesmvvmkotlinv3.model.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.Result
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.awaitTaskCompletable
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.awaitTaskResult
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.toFirebaseNote
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.toNote
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.toNoteListFromRoomNote
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.toRoomNote
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.toUser
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.FirebaseNote
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.Note
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.NoteDao
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.User
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.repository.INoteRepository

private const val COLLECTION_NAME = "notes"

class NoteRepoImpl(
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    val remote: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val local: NoteDao
) : INoteRepository {

    override suspend fun getNoteById(noteId: String): Result<Exception, Note> {
        val user = getActiveUser()
        return if (user != null) getRemoteNote(noteId, user)
        else getLocalNote(noteId)
    }

    override suspend fun getNotes(): Result<Exception, List<Note>> {
        val user = getActiveUser()
        return if (user != null) getRemoteNotes(user)
        else getLocalNotes()
    }

    override suspend fun deleteNote(note: Note): Result<Exception, Unit> {
        val user = getActiveUser()
        return if (user != null) deleteRemoteNote(note.copy(creator = user))
        else deleteLocalNote(note)
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> {
        val user = getActiveUser()
        return if (user != null) updateRemoteNote(note.copy(creator = user))
        else updateLocalNote(note)
    }

    private fun getActiveUser(): User? {
        return firebaseAuth.currentUser?.toUser
    }

    private suspend fun getRemoteNote(creationDate: String, user: User): Result<Exception, Note> {
        return try {
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .document(creationDate + user.uid)
                    .get()
            )

            Result.build {
                task.toObject(FirebaseNote::class.java)?.toNote ?: throw Exception()
            }
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

    private suspend fun getRemoteNotes(user: User): Result<Exception, List<Note>> {
        return try {
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .whereEqualTo("creator", user.uid)
                    .get()
            )
            resultToNoteList(task)
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

    private fun resultToNoteList(result: QuerySnapshot?): Result<Exception, List<Note>> {
        val noteList = mutableListOf<Note>()

        result?.forEach { documentSnapshot ->
            noteList.add(documentSnapshot.toObject(FirebaseNote::class.java).toNote)
        }

        return Result.build { noteList }
    }

    private suspend fun deleteRemoteNote(note: Note): Result<Exception, Unit> = Result.build{
        awaitTaskCompletable(
            remote.collection(COLLECTION_NAME)
                .document(note.creationDate + note.creator!!.uid)
                .delete()
        )
    }

    private suspend fun updateRemoteNote(note: Note): Result<Exception, Unit> {
        return try {
            awaitTaskCompletable(
                remote.collection(COLLECTION_NAME)
                    .document(note.creationDate + note.creator!!.uid)
                    .set(note.toFirebaseNote)
            )
            Result.build { Unit }
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

    private suspend fun getLocalNote(noteId: String): Result<Exception, Note> = Result.build {
        local.getNoteById(noteId).toNote
    }

    private suspend fun getLocalNotes(): Result<Exception, List<Note>> = Result.build {
        local.getNotes().toNoteListFromRoomNote()
    }

    private suspend fun deleteLocalNote(note: Note): Result<Exception, Unit> = Result.build {
        local.deleteNote(note.toRoomNote)
        Unit
    }

    private suspend fun updateLocalNote(note: Note): Result<Exception, Unit> = Result.build {
        local.insertOrUpdateNote(note.toRoomNote)
        Unit
    }
}

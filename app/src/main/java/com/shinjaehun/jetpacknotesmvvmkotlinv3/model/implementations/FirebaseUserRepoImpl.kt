package com.shinjaehun.jetpacknotesmvvmkotlinv3.model.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.Result
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.awaitTaskCompletable
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.User
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.repository.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseUserRepoImpl(
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : IUserRepository {

    override suspend fun signInGoogleUser(idToken: String): Result<Exception, Unit>
        = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            awaitTaskCompletable(auth.signInWithCredential(credential))
            Result.build { Unit }
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

    override suspend fun signOutCurrentUser(): Result<Exception, Unit> {
        return Result.build {
            auth.signOut()
        }
    }

    override suspend fun getCurrentUser(): Result<Exception, User?> {
        val firebaseUser = auth.currentUser

        return if (firebaseUser == null) {
            Result.build { null }
        } else {
            Result.build {
                User(
                    firebaseUser.uid,
                    firebaseUser.displayName ?: ""
                )
            }
        }
    }
}
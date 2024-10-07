package com.shinjaehun.jetpacknotesmvvmkotlinv3.model.repository

import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.Result
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.User

interface IUserRepository {
    suspend fun getCurrentUser(): Result<Exception, User?>
    suspend fun signOutCurrentUser(): Result<Exception, Unit>
    suspend fun signInGoogleUser(idToken: String): Result<Exception, Unit>
}
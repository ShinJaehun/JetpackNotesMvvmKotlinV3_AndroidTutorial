package com.shinjaehun.jetpacknotesmvvmkotlinv3.login.buildlogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.implementations.FirebaseUserRepoImpl
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.repository.IUserRepository

class LoginInjector(application: Application): AndroidViewModel(application) {

    init {
        FirebaseApp.initializeApp(application)
    }

    private fun getUserRepository(): IUserRepository {
        return FirebaseUserRepoImpl()
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory =
        LoginViewModelFactory(getUserRepository())
}
package com.shinjaehun.jetpacknotesmvvmkotlinv3.login.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shinjaehun.jetpacknotesmvvmkotlinv3.login.LoginViewModel
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.repository.IUserRepository
import kotlinx.coroutines.Dispatchers

class LoginViewModelFactory(
    private val userRepo: IUserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(userRepo, Dispatchers.Main) as T
    }
}
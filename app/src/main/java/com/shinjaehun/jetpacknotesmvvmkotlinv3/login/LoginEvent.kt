package com.shinjaehun.jetpacknotesmvvmkotlinv3.login

import android.content.Context

sealed class LoginEvent {
    data class OnAuthButtonClick(val context: Context) : LoginEvent()
    object OnStart : LoginEvent()
}
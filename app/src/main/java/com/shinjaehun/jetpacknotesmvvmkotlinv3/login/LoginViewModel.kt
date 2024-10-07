package com.shinjaehun.jetpacknotesmvvmkotlinv3.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.shinjaehun.jetpacknotesmvvmkotlinv3.BuildConfig
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.ANTENNA_EMPTY
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.ANTENNA_FULL
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.ANTENNA_LOOP
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.BaseViewModel
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.LOADING
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.LOGIN_ERROR
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.Result
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.SIGNED_IN
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.SIGNED_OUT
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.SIGN_IN
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.SIGN_OUT
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.User
import com.shinjaehun.jetpacknotesmvvmkotlinv3.model.repository.IUserRepository
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val TAG = "LoginViewModel"

class LoginViewModel(
    val repo: IUserRepository,
    uiContext: CoroutineContext
) : BaseViewModel<LoginEvent>(uiContext) {

    private val userState = MutableLiveData<User?>()

//    internal val authAttempt = MutableLiveData<Unit>()
    internal val startAnimation = MutableLiveData<Unit>()

    internal val signInStatusText = MutableLiveData<String>()
    internal val authButtonText = MutableLiveData<String>()
    internal val satelliteDrawable = MutableLiveData<String>()

    private fun showErrorState() {
        Log.i(TAG, "showErrorState")
        signInStatusText.value = LOGIN_ERROR
        authButtonText.value = SIGN_IN
        satelliteDrawable.value = ANTENNA_EMPTY
    }

    private fun showLoadingState() {
        Log.i(TAG, "showLoadingState")
        signInStatusText.value = LOADING
        satelliteDrawable.value = ANTENNA_LOOP
        startAnimation.value = Unit // 이렇게 해서 애니메이션이 시작된다는 게 신기!
    }

    private fun showSignedInState() {
        Log.i(TAG, "showSignedInState")
        signInStatusText.value = SIGNED_IN
        authButtonText.value = SIGN_OUT
        satelliteDrawable.value = ANTENNA_FULL
    }

    private fun showSignedOutState() {
        Log.i(TAG, "showSignedOutState")
        signInStatusText.value = SIGNED_OUT
        authButtonText.value = SIGN_IN
        satelliteDrawable.value = ANTENNA_EMPTY
    }

    override fun handleEvent(event: LoginEvent) {
        showLoadingState()
        when (event) {
            is LoginEvent.OnStart -> getUser()
            is LoginEvent.OnAuthButtonClick -> onAuthButtonClick(event.context)
        }
    }

    private fun getUser() = launch {
        Log.i(TAG, "LoginViewModel getUser(): ${Thread.currentThread().name}")

        val result = repo.getCurrentUser()
        when(result) {
            is Result.Value -> {
                userState.value = result.value
                if (result.value == null) showSignedOutState()
                else showSignedInState()
            }
            is Result.Error -> showErrorState()
        }
    }

    private fun onAuthButtonClick(context: Context) {
        if (userState.value == null) {
            googleSignIn(context)
        }
        else signOutUser()
    }

    private fun googleSignIn(context: Context) {
        val credentialManager: CredentialManager = CredentialManager.create(context)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(BuildConfig.API_KEY)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            Log.i(TAG, "LoginViewModel googleSignIn(): ${Thread.currentThread().name}")

            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                    val createGoogleUserResult = repo.signInGoogleUser(googleIdTokenCredential.idToken)
                    if (createGoogleUserResult is Result.Value) getUser()
                    else showErrorState()
                }
            } catch (e: Exception) {
                showErrorState()
            }
        }
    }

    private fun signOutUser() = launch {
        Log.i(TAG, "LoginViewModel signOutUser(): ${Thread.currentThread().name}")
        val result = repo.signOutCurrentUser()

        when (result) {
            is Result.Value -> {
                userState.value = null
                showSignedOutState()
            }
            is Result.Error -> showErrorState()
        }
    }


}
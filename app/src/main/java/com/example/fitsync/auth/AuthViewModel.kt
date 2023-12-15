package com.example.fitsync.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    private val _authenticationState = MutableStateFlow<AuthenticationState?>(null)
    val authenticationState = _authenticationState.asStateFlow()

    // Expose the Google Sign-In Intent to the UI
    val signInIntent: Intent get() = googleSignInClient.signInIntent

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Process the Google Sign-In result
    fun processGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                authenticateWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // user didn't select any google account to login to, just do nothing
            }
        }
    }

    private fun authenticateWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val authResult = firebaseAuthRepository.firebaseAuthWithGoogle(idToken).await()
                if (authResult.user != null) {
                    android.util.Log.d("AuthViewModel", "user logged in here")
                    // Authentication was successful
                    _authenticationState.value = AuthenticationState.AUTHENTICATED
                } else {
                    // Authentication was unsuccessful (user is null)
                    _authenticationState.value = AuthenticationState.UNAUTHENTICATED
                }
            } catch (e: ApiException) {
                android.util.Log.d("AuthViewModel", "Authentication failed: ${e.localizedMessage}")

                // Notify UI about the authentication failure
                _authenticationState.value = AuthenticationState.ERROR
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Possible states of authentication
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, ERROR
    }

}

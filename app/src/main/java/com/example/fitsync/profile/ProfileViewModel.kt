package com.example.fitsync.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitsync.auth.FirebaseAuthRepository
import com.example.fitsync.user.FirestoreUserRepository
import com.example.fitsync.user.User
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    firestoreUserRepository: FirestoreUserRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Expose currentUserData as StateFlow
    val currentUserData: StateFlow<User?> = firestoreUserRepository.currentUserData
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun logout(): Task<Void> {
        _isLoading.value = true
        return googleSignInClient.signOut().also {
            firebaseAuthRepository.signOut()
        }
    }
}

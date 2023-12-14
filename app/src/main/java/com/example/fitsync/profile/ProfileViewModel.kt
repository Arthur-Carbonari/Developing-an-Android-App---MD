package com.example.fitsync.profile

import androidx.lifecycle.ViewModel
import com.example.fitsync.auth.FirebaseAuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    ) : ViewModel() {

    fun logout(): Task<Void> {
        return googleSignInClient.signOut().also { firebaseAuthRepository.signOut() }
    }
}

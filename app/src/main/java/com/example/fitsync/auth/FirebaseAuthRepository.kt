package com.example.fitsync.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun firebaseAuthWithGoogle(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth.signInWithCredential(credential)
    }

    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
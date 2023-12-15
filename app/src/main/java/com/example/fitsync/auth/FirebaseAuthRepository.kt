package com.example.fitsync.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

// Repository for managing Firebase authentication
class FirebaseAuthRepository @Inject constructor() {

    // Instance of FirebaseAuth for authentication operations.
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Flow that emits the current Firebase user's authentication state.
    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }

        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    // Authenticates the user with Google ID token using Firebase.
    fun firebaseAuthWithGoogle(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth.signInWithCredential(credential)
    }

    // Checks if a user is currently signed in.
    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    // Signs out the current user from Firebase.
    fun signOut() {
        firebaseAuth.signOut()
    }

    // Retrieves the current user's UID if logged in.
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}
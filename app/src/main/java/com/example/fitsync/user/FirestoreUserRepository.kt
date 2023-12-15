package com.example.fitsync.user

import com.example.fitsync.auth.FirebaseAuthRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Manages user data interactions with Firestore.
// Uses Dagger Hilt for dependency injection.
class FirestoreUserRepository @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    db: FirebaseFirestore,
) {

    // Reference to the 'users' collection in Firestore
    private val usersCollection = db.collection("users")

    // A shared flow to trigger refreshes of the current user's data.
    private val refreshTrigger = MutableSharedFlow<Unit>()

    // A Flow that emits the current user's data. Combines authentication state with a refresh trigger.
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUserData: Flow<User?> = firebaseAuthRepository.authStateFlow
        .combine(refreshTrigger.onStart { emit(Unit) }) { user, _ -> user }
        .flatMapLatest { firebaseUser ->
            if (firebaseUser != null) getUserDataFlow(firebaseUser) else flowOf(null)
        }
        .distinctUntilChanged()


    // Triggers a refresh of the current user's data.
    suspend fun refreshCurrentUser() {
        refreshTrigger.emit(Unit)
    }

    // Creates a Flow to fetch and emit a specific user's data from Firestore.
    private fun getUserDataFlow(firebaseUser: FirebaseUser): Flow<User?> = flow {
        val userId = firebaseUser.uid
        val documentSnapshot = usersCollection.document(userId).get().await()

        val user = if (documentSnapshot.exists()) {
            // Document exists, convert it to User object
            documentSnapshot.toObject(User::class.java)
        } else {
            // Document does not exist, create a new User object from FirebaseUser
            val newUser = User(
                id = userId,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                photoUrl = firebaseUser.photoUrl.toString(),
                )

            // Add the new user to Firestore
            usersCollection.document(userId).set(newUser).await()
            newUser
        }

        emit(user)
    }

    // Updates the user's height, weight, and goal in Firestore.
    fun updateUserDetails(height: Int, weight: Int, goal: Int): Task<Void> {
        val userId = firebaseAuthRepository.getCurrentUserId()
        return if (userId != null) {
            val userDocument = usersCollection.document(userId)
            userDocument.update(mapOf("height" to height, "weight" to weight, "goal" to goal))
        } else {
            Tasks.forException(Exception("User not logged in"))
        }

    }

}

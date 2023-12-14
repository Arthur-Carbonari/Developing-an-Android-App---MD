package com.example.fitsync.user

import com.example.fitsync.auth.FirebaseAuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserRepository @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    val currentUserData: Flow<User?> = firebaseAuthRepository.authStateFlow
        .flatMapLatest { firebaseUser ->
            if (firebaseUser != null) getUserDataFlow(firebaseUser) else flowOf(null)
        }

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

    fun getUser(userId: String): Task<DocumentSnapshot> {
        return usersCollection.document(userId).get()
    }

    fun addUser(user: User): Task<Void> {
        return usersCollection.document(user.id).set(user)
    }

    fun updateUser(userId: String, userUpdateMap: Map<String, Any>): Task<Void> {
        return usersCollection.document(userId).update(userUpdateMap)
    }

    fun deleteUser(userId: String): Task<Void> {
        return usersCollection.document(userId).delete()
    }
}

package com.example.fitsync.steps

import com.example.fitsync.auth.FirebaseAuthRepository
import com.example.fitsync.steps.models.DaySteps
import com.example.fitsync.steps.models.WeekSteps
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreStepRepository @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    db: FirebaseFirestore,
    ) {
    private val usersCollection = db.collection("users")

    fun saveStepsForDay(weekId: String, daySteps: DaySteps) {
        val userId = firebaseAuthRepository.getCurrentUserId()
        if (userId != null) {
            val weekDocument = usersCollection
                .document(userId)
                .collection("weeks")
                .document(weekId)

            weekDocument.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Update the existing document
                    weekDocument.update("days.${daySteps.date}", daySteps.toMap())
                } else {
                    // Create a new week document
                    val newWeek = WeekSteps(weekId, mapOf(DayOfWeek.valueOf(daySteps.date) to daySteps))
                    weekDocument.set(newWeek.toFirestoreMap())
                }
            }
        }
    }

    suspend fun getOrCreateWeekData(weekId: String): WeekSteps {
        val userId = firebaseAuthRepository.getCurrentUserId()
        val weekData = if (userId != null) {
            val weekDocumentRef = usersCollection
                .document(userId)
                .collection("weeks")
                .document(weekId)

            val weekDocument = weekDocumentRef.get().await()
            if (weekDocument.exists()) {
                WeekSteps.fromFirestore(weekDocument)
            } else {
                createEmptyWeek(weekId).also { emptyWeek ->
                    weekDocumentRef.set(emptyWeek.toFirestoreMap()).await()
                }
            }
        } else {
            createEmptyWeek(weekId)
        }

        // Sort the days of the week
        val sortedDays = weekData.days.toSortedMap(compareBy { DayOfWeek.valueOf(it.toString()) })
        return weekData.copy(days = sortedDays)
    }

    private fun createEmptyWeek(weekId: String): WeekSteps {
        val emptyDays = DayOfWeek.values().associateWith { DaySteps(it.toString(), 0) }
        return WeekSteps(weekId, emptyDays)
    }
}

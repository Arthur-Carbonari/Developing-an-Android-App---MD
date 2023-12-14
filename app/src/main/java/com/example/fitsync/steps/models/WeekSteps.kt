package com.example.fitsync.steps.models

import java.time.DayOfWeek
import com.google.firebase.firestore.DocumentSnapshot

data class WeekSteps(
    val weekId: String = "",
    val days: Map<DayOfWeek, DaySteps> = emptyMap()
) {
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "weekId" to weekId,
            "days" to days.mapKeys { it.key.name }
        )
    }

    companion object {
        fun fromFirestore(document: DocumentSnapshot): WeekSteps {
            val weekId = document.id
            val daysMap = document.data?.get("days") as? Map<String, Any> ?: emptyMap()
            val days = daysMap.mapKeys { DayOfWeek.valueOf(it.key) }
                .mapValues { DaySteps.fromMap(it.value as Map<String, Any>) }

            return WeekSteps(weekId, days)
        }
    }
}

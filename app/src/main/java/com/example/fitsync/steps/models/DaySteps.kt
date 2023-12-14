package com.example.fitsync.steps.models

data class DaySteps(
    val date: String, // Format: "YYYY-MM-DD"
    val steps: Int
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "date" to date,
            "steps" to steps
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): DaySteps {
            return DaySteps(
                map["date"] as String,
                (map["steps"] as Number).toInt()
            )
        }
    }
}
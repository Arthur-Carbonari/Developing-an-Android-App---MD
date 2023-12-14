package com.example.fitsync.steps
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitsync.steps.models.DaySteps
import com.example.fitsync.utils.getWeekId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class StepCounterRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firestoreStepRepository: FirestoreStepRepository,
) {
    private val _stepsFlow = MutableStateFlow(0)
    val stepsFlow: StateFlow<Int> = _stepsFlow.asStateFlow()

    private var currentDay = LocalDate.now()

    init {
        val storedDay = sharedPreferences.getString("currentDay", null)
        val storedDayDate = LocalDate.parse(storedDay)
        if (storedDayDate != currentDay) {
            storePreviousDaySteps(storedDayDate)
            startCurrentDay()
        } else {
            _stepsFlow.value = sharedPreferences.getInt("steps", 0)
        }
    }

    fun incrementSteps() {
        _stepsFlow.value++
        val newDay = LocalDate.now()
        if (newDay != currentDay) {
            storePreviousDaySteps(currentDay)
            currentDay = newDay
            startCurrentDay()
        } else if (_stepsFlow.value % 50 == 0) {
            updateSharedPreferences()
        }
    }

    private fun storePreviousDaySteps(date: LocalDate) {
        val weekId = date.getWeekId()
        val daySteps = DaySteps(date.toString(), _stepsFlow.value)
        firestoreStepRepository.saveStepsForDay(weekId, daySteps)
    }

    private fun updateSharedPreferences() {
        sharedPreferences.edit()
            .putInt("steps", _stepsFlow.value)
            .putString("currentDay", currentDay.toString())
            .apply()
    }

    private fun startCurrentDay() {
        _stepsFlow.value = 0
        updateSharedPreferences()
    }

    /**
     * Saves the current step count to both SharedPreferences and Firestore.
     */
    fun saveCurrentSteps() {
        // Update SharedPreferences with the current steps
        updateSharedPreferences()

        // Save the steps to Firestore
        val weekId = currentDay.getWeekId()
        val daySteps = DaySteps(currentDay.toString(), _stepsFlow.value)
        firestoreStepRepository.saveStepsForDay(weekId, daySteps)
    }
}

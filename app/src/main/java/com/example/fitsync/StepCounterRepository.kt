package com.example.fitsync
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StepCounterRepository {
    private val _stepsFlow = MutableStateFlow(0)
    val stepsFlow: StateFlow<Int> = _stepsFlow.asStateFlow()

    private lateinit var sharedPreferences: SharedPreferences

    // stores the day the current step count is related to
    private var currentDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Initializes the step count and starts the current day if its not started yet
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)

        val stepsCurrentDay = sharedPreferences.getString("currentDay", "")

        if (stepsCurrentDay != currentDay) startCurrentDay()
        else _stepsFlow.value = sharedPreferences.getInt("steps", 0)

    }

    // Increments step count and updates shared preferences
    fun incrementSteps() {
        // this is run every 50 steps
        if(_stepsFlow.value % 50 == 0) {
            // first we check if the current day is correct
            val actualCurrentDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            if (actualCurrentDay != currentDay){
                // if it is not we start the current day
                currentDay = actualCurrentDay
                startCurrentDay()
            }
            // every 50 steps we increment the steps normally and then save the amount to shared preference
            _stepsFlow.value++
            sharedPreferences.edit().putInt("steps", _stepsFlow.value).apply()
            return
        }

        // normally we just increment the step count
        _stepsFlow.value++
    }

    // This method will update shared memory currentDay and set the steps to 0
    private fun startCurrentDay() {
        _stepsFlow.value = 0
        sharedPreferences.edit()
            .putInt("steps", 0)
            .putString("currentDay", currentDay)
            .apply()
    }
}

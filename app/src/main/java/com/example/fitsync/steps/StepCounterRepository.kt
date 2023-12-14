package com.example.fitsync.steps
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepCounterRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val _stepsFlow = MutableStateFlow(0)
    val stepsFlow: StateFlow<Int> = _stepsFlow.asStateFlow()

    private var currentDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        val stepsCurrentDay = sharedPreferences.getString("currentDay", "")
        if (stepsCurrentDay != currentDay) startCurrentDay()
        else _stepsFlow.value = sharedPreferences.getInt("steps", 0)
    }

    fun incrementSteps() {
        if (_stepsFlow.value % 50 == 0) {
            val actualCurrentDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            if (actualCurrentDay != currentDay) {
                currentDay = actualCurrentDay
                startCurrentDay()
            }
            _stepsFlow.value++
            sharedPreferences.edit().putInt("steps", _stepsFlow.value).apply()
            return
        }
        _stepsFlow.value++
    }

    private fun startCurrentDay() {
        _stepsFlow.value = 0
        sharedPreferences.edit()
            .putInt("steps", 0)
            .putString("currentDay", currentDay)
            .apply()
    }
}

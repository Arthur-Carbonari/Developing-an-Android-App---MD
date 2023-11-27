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

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)

        val stepsCurrentDay = sharedPreferences.getString("currentDay", "")
        val actualCurrentDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (stepsCurrentDay != actualCurrentDay) {
            _stepsFlow.value = 0
            sharedPreferences.edit()
                .putInt("steps", 0)
                .putString("currentDay", actualCurrentDay)
                .apply()
        } else {
            _stepsFlow.value = sharedPreferences.getInt("steps", 0)
        }
    }

    fun updateSteps(steps: Int) {
        _stepsFlow.value = steps
        sharedPreferences.edit().putInt("steps", steps).apply()
    }
}

package com.example.fitsync
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow

object StepCounterRepository {
    private val _stepsFlow = MutableStateFlow(0)
    val stepsFlow: StateFlow<Int> = _stepsFlow.asStateFlow()

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
        _stepsFlow.value = sharedPreferences.getInt("steps", 0)
    }

    fun updateSteps(steps: Int) {
        _stepsFlow.value = steps
        sharedPreferences.edit().putInt("steps", steps).apply()
    }
}

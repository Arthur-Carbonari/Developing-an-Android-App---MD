package com.example.fitsync.home

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitsync.steps.FirestoreStepRepository
import com.example.fitsync.steps.StepCounterRepository
import com.example.fitsync.steps.StepCounterService
import com.example.fitsync.steps.models.DaySteps
import com.example.fitsync.user.FirestoreUserRepository
import com.example.fitsync.utils.getWeekId
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

// Manages step counting and user data interactions. Uses Hilt for dependency injection.
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stepCounterRepository: StepCounterRepository,
    private val firestoreStepRepository: FirestoreStepRepository,
    private val firestoreUserRepository: FirestoreUserRepository
) : ViewModel() {

    // Exposes the current step count as an immutable StateFlow.
    @RequiresApi(Build.VERSION_CODES.O)
    val stepsFlow: StateFlow<Int> = stepCounterRepository.stepsFlow

    // Updates user details (height, weight, goal) in Firestore.
    fun updateUserDetails(height: Int, weight: Int, goal: Int): Task<Void> {
        return firestoreUserRepository.updateUserDetails(height, weight, goal)
    }

    // MutableStateFlow to manage current week's steps data.
    private val _currentWeekStats = MutableStateFlow<Map<DayOfWeek, DaySteps>>(emptyMap())
    val currentWeekStats: StateFlow<Map<DayOfWeek, DaySteps>> = _currentWeekStats

    // Flow for the goal value, derived from the current user data
    val goalFlow: StateFlow<Int?> = firestoreUserRepository.currentUserData
        .map { user -> user?.goal }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Fetches the current week's steps data on initialization.
    init { fetchCurrentWeekSteps() }

    // Fetches and updates the current week's steps data.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchCurrentWeekSteps() {
        viewModelScope.launch {
            val weekId = LocalDate.now().getWeekId()
            val weekSteps = firestoreStepRepository.getOrCreateWeekData(weekId)

            _currentWeekStats.value = weekSteps.days
        }
    }

    // Toggles the step counter service on or off based on its current state.
    fun toggleStepCounterService(context: Context, isServiceRunning: Boolean): Boolean {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        return if (isServiceRunning) {
            // Stop the service
            context.stopService(serviceIntent)
            false
        } else {
            // Start the service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            true
        }
    }

    // Triggers a refresh of the current user data in Firestore.
    fun triggerRefresh() {
        viewModelScope.launch { firestoreUserRepository.refreshCurrentUser() }
    }
}
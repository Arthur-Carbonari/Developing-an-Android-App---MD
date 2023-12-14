package com.example.fitsync.home

import androidx.lifecycle.ViewModel
import com.example.fitsync.steps.StepCounterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    stepCounterRepository: StepCounterRepository
) : ViewModel() {
    val stepsFlow: StateFlow<Int> = stepCounterRepository.stepsFlow
}
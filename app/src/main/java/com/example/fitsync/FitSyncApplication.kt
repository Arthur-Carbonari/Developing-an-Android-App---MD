package com.example.fitsync

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Main application class for the FitSync app.
// Configures Hilt dependency injection framework for the entire application.
@HiltAndroidApp
class FitSyncApplication: Application() {
}
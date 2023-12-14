package com.example.fitsync
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fitsync.auth.AuthActivity

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen().apply {
            setKeepOnScreenCondition { true }
        }

        super.onCreate(savedInstanceState)

        // Navigate to MainActivity after initialization is complete.
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}
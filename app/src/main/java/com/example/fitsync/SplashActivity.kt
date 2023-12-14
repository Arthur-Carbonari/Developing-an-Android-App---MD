package com.example.fitsync
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fitsync.auth.AuthActivity
import com.example.fitsync.auth.FirebaseAuthRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuthRepository: FirebaseAuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen().apply { setKeepOnScreenCondition { true } }

        super.onCreate(savedInstanceState)

        // Check if the user is signed in and navigate accordingly
        if (firebaseAuthRepository.isUserSignedIn()) {
            navigateToMainActivity()
        } else {
            navigateToAuthActivity()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}

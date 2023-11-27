package com.example.fitsync

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitsync.ui.theme.FitSyncTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureGoogleSignIn()

        setContent {
            FitSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(::signIn)
                }
            }
        }

        startStepCounterService()
    }

    /**
     * Configures Google Sign-In options and initializes the GoogleSignInClient.
     * It sets up the request for the user's ID token and email.
     */
    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * Starts the Google Sign-In process. It first signs out any existing user
     * to ensure the sign-in dialog is displayed every time. Then, it initiates
     * the sign-in intent, allowing the user to choose a Google account to sign in with.
     * @return Task representing the pending result of the sign-out operation.
     */
    private fun signIn(): Task<Void> {
        // First, sign out from any existing session
        return googleSignInClient.signOut().addOnCompleteListener {
            // After signing out, start the sign-in process
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    /**
     * Handles the result from the Google Sign-In activity. If the result is successful,
     * it retrieves the signed-in account and proceeds to authenticate with Firebase.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Handle exception
            }
        }
    }

    /**
     * Authenticates the user with Firebase using the ID token from Google Sign-In.
     * On successful authentication, it updates the UI with the signed-in user's information.
     * @param idToken The ID token from Google Sign-In used for authenticating with Firebase.
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                } else {
                    // If sign in fails, display a message to the user.
                }
            }
    }

    /**
     * Starts the StepCounterService. If the OS version is Oreo or above,
     * it starts the service as a foreground service; otherwise, it starts it as a background service.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startStepCounterService() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}

// Preview function
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    FitSyncTheme{
    }
}

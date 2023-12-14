package com.example.fitsync.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitsync.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthScreen()
        }
    }
}

@Composable
fun AuthScreen(authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val authenticationState by authViewModel.authenticationState.collectAsState()

    when (authenticationState) {
        AuthViewModel.AuthenticationState.AUTHENTICATED -> {
            LaunchedEffect(Unit) {
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as? Activity)?.finish()
            }
        }
        AuthViewModel.AuthenticationState.ERROR -> {
            ErrorDialog()
        }
        else -> {}
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            authViewModel.processGoogleSignInResult(result.data)
        }
    )

    Button(onClick = {
        googleSignInLauncher.launch(authViewModel.signInIntent)
    }) {
        Text("Sign in with Google")
    }
}


@Composable
fun ErrorDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Error") },
        text = { Text("An error occurred during authentication.") },
        confirmButton = {
            Button(onClick = { /* TODO handle confirmation action */ }) {
                Text("OK")
            }
        }
    )
}
package com.example.fitsync.auth

 import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Spacer
 import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitsync.MainActivity
import com.example.fitsync.R
import com.example.fitsync.ui.theme.FitSyncTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthScreen()
                }
            }
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

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 50.dp),
        verticalArrangement = Arrangement.SpaceAround,
    ) {

        Image(
            painter = painterResource(id = R.drawable.banner_img),
            contentDescription = "banner"
        )

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Welcome to FitSync", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(10.dp))
            Text("Let us help you with your health journey", style = MaterialTheme.typography.titleLarge)
        }


        Button(
            onClick = {
                googleSignInLauncher.launch(authViewModel.signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, end = 16.dp)
                .background(Color.Black),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = Color.White
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = ""
            )
            Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
        }

    }

}


@Composable
fun ErrorDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Error") },
        text = { Text("An error occurred during authentication.") },
        confirmButton = {
            Button(onClick = { }) {
                Text("OK")
            }
        }
    )
}
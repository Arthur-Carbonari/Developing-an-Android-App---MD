package com.example.fitsync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task

/**
 * Composable function for the registration screen.
 * It allows the user to enter personal information and register in the app.
 * @param navController NavController for navigating between screens.
 * @param onSignInClick Lambda function triggered for signing in with Google.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavController, onSignInClick: () -> Task<Void>) {

    //For now  this is always the first screen the user sees, later it will only show up if the
    //the user is not logged in

    // variables that store values form the form
    var name by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var stepsGoal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(){
            Column(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Welcome", style = MaterialTheme.typography.displaySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = stepsGoal,
                    onValueChange = { stepsGoal = it },
                    label = { Text("Daily Steps Goal") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        registerUser(name, height, weight, stepsGoal, navController)
                    })
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        onSignInClick().addOnCompleteListener {
                            registerUser(name, height, weight, stepsGoal, navController)
                        }
                    }
                ) {
                    Text("Register", Modifier.padding(2.dp), style = MaterialTheme.typography.titleLarge)

                }
            }

        }

    }

}

/**
 * Function to handle user registration.
 * It logs the user's input and navigates to the home screen of the app.
 * @param name The name entered by the user.
 * @param height The height of the user in centimeters.
 * @param weight The weight of the user in kilograms.
 * @param stepsGoal The daily step goal set by the user.
 * @param navController NavController for navigating between screens.
 */
private fun registerUser(
    name: String,
    height: String,
    weight: String,
    stepsGoal: String,
    navController: NavController,
) {
    // For now we just log the user's input here. later the data will be saved
    print("$name, Height: $height, Weight: $weight, Steps Goal: $stepsGoal")

    // Navigate to the app home screen
    navController.navigate(AppRoutes.HOME.name) {
        // Clear the back stack so the user cannot navigate back to the registration screen
    }
}
package com.example.fitsync.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.fitsync.steps.StepCounterService
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Composable function for the Home screen of the app.
 * It displays a summary of the user's activities, the current date, and interactive cards for different features.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    // Placeholder data

    val date = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.padding(16.dp).verticalScroll(scrollState)
    ) {
        // Header
        Text(
            text = "Summary",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = dateFormatter.format(date),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        ActivityCard(homeViewModel.stepsFlow)
        Spacer(modifier = Modifier.height(16.dp))

        WeekRecap(homeViewModel.currentWeekStats)
        Spacer(modifier = Modifier.height(16.dp))

        DailyChallengeCard()
        Spacer(modifier = Modifier.height(16.dp))

        // Bonus Points Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row (modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), Arrangement.SpaceBetween) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("One step at a time", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("With Gratitude", style = MaterialTheme.typography.bodyLarge)

                }
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    StartStopStepCounterButton(homeViewModel)
                }
            }

        }

    }
}



/**
 * Composable function that displays an activity card.
 * The card shows the user's current step count, distance covered, and calories burned.
 */
@Composable
fun ActivityCard(stepsFlow: StateFlow<Int>){
    val steps by stepsFlow.collectAsState()

    val goal = 1200
    val averageStepLengthMeters = 175 * 0.4 / 100

    val distance = steps * averageStepLengthMeters / 1000 // Convert to kilometers
    val calories = steps * 0.04 // 0.04 kcal per step

    // Activity Summary Section
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column() {
                Text("Activity", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                Text("$steps/$goal Steps", style = MaterialTheme.typography.bodyLarge)
                Text("${String.format("%.2f", distance)} Km Distance", style = MaterialTheme.typography.bodyLarge)
                Text("${String.format("%.2f", calories)} Kcal", style = MaterialTheme.typography.bodyLarge)
            }

            CustomCircularProgressIndicator(
                progress = (steps / goal.toFloat()),
                modifier = Modifier.width(100.dp),
            )

        }
    }
}

/**
 * Composable function to display a daily recap card for a specific day of the week.
 * It visualizes the user's activity progress for that day.
 * @param progress The activity progress for the day, represented as a float value.
 * @param dayOfWeek The day of the week for which the recap is displayed.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyRecap(progress: Float, dayOfWeek: DayOfWeek){

    Card(
        modifier = Modifier.width(150.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = (dayOfWeek.name), style = MaterialTheme.typography.titleMedium,
                modifier= Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(5.dp))
            CustomCircularProgressIndicator(progress = progress)
        }
    }
}

/**
 * Data class representing the statistics for a single day.
 * Used to hold and pass around the day's activity progress and the associated day of the week.
 * @param dayOfWeek The day of the week.
 * @param progress The activity progress for the day, represented as a float value.
 */
data class DayStats(
    val dayOfWeek: DayOfWeek,
    val progress: Float,
)

/**
 * Composable function that displays a horizontal row of daily recap cards.
 * Each card represents the user's activity progress for a specific day of the week.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekRecap(weekStats: StateFlow<List<DayStats>>) {
    val weekStatsState by weekStats.collectAsState()

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(weekStatsState) { dayOfWeek ->
            DailyRecap(progress = dayOfWeek.progress, dayOfWeek = dayOfWeek.dayOfWeek)
        }
    }
}

/**
 * Composable function for displaying a daily challenge card.
 * The card motivates the user with a daily step goal and offers a button to start the challenge.
 */
@Composable
fun DailyChallengeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Daily Challenge",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Take 10,000 steps today to complete the challenge and earn rewards!",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Start Challenge")
            }
        }
    }
}

/**
 * Custom composable function for displaying a circular progress indicator.
 * It shows the user's progress towards a goal in a circular format and displays the percentage value.
 * @param progress The current progress towards the goal, represented as a float value.
 * @param modifier Modifier for customizing the layout and appearance.
 * @param strokeWidth The stroke width for the circular progress bar.
 */
@Composable
fun CustomCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 7.dp
) {

    val result = (progress * 100).toInt().coerceAtMost(100)

    Column(modifier, Arrangement.Center, Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = Color.Green.copy(alpha = 0.6f),
            strokeWidth =strokeWidth,
            progress = progress,
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "  $result%",
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StartStopStepCounterButton(homeViewModel: HomeViewModel) {
    var isServiceRunning by remember { mutableStateOf(StepCounterService.isRunning) }
    val context = LocalContext.current

    // Prepare the permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, start or stop the service
                homeViewModel.toggleStepCounterService(context, isServiceRunning).also {
                    isServiceRunning = it
                }
            } else {
                // Permission denied, handle the denial
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Button(onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, check and request permission
            if (ContextCompat.checkSelfPermission( context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted
                homeViewModel.toggleStepCounterService(context, isServiceRunning).also {
                    isServiceRunning = it
                }
            } else {
                // Request permission
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        } else {
            // For below Android 10, permission is not needed
            homeViewModel.toggleStepCounterService(context, isServiceRunning).also {
                isServiceRunning = it
            }
        }
    }) {
        Text(if (isServiceRunning) "Stop Counter" else "Start Counter")
    }
}

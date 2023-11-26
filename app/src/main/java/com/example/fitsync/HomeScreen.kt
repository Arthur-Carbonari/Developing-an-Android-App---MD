package com.example.fitsync

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import java.time.DayOfWeek
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    // Placeholder data

    val date = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

    Column(
        modifier = Modifier.padding(16.dp)
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

        ActivityCard()
        Spacer(modifier = Modifier.height(16.dp))

        WeekRecap()
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
                    Text("Bonus Points", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Write Gratitude", style = MaterialTheme.typography.bodyLarge)

                }
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Button(onClick = { }) {
                        Text("Write Now")
                    }
                }
            }

        }
    }
}

@Composable
fun ActivityCard(){
    val steps by StepCounterRepository.stepsFlow.collectAsState()
    val goal = 1200
    val distance = 0.69f
    val calories = 24

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

                Text("$calories/300 CAL", style = MaterialTheme.typography.bodyLarge)
                Text("$steps Steps", style = MaterialTheme.typography.bodyLarge)
                Text("${distance}KM Distance", style = MaterialTheme.typography.bodyLarge)
            }

            CustomCircularProgressIndicator(
                progress = (steps / goal.toFloat()),
                modifier = Modifier.width(100.dp),
            )

        }
    }
}

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

data class DayStats(
    val dayOfWeek: DayOfWeek,
    val progress: Float,
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekRecap() {
    val daysOfWeekStats = listOf(
        DayStats(DayOfWeek.MONDAY, 0.6f),
        DayStats(DayOfWeek.TUESDAY, 0.5f),
        DayStats(DayOfWeek.WEDNESDAY, 0.8f),
        DayStats(DayOfWeek.THURSDAY, 0.3f),
        DayStats(DayOfWeek.FRIDAY, 0.7f),
        DayStats(DayOfWeek.SATURDAY, 0.2f),
        DayStats(DayOfWeek.SUNDAY, 0.9f)
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(daysOfWeekStats) { dayOfWeek ->
            DailyRecap(progress = dayOfWeek.progress, dayOfWeek = dayOfWeek.dayOfWeek)
        }
    }
}

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

@Composable
fun CustomCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 7.dp
) {

    Column(modifier, Arrangement.Center, Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = Color.Green.copy(alpha = 0.6f),
            strokeWidth =strokeWidth,
            progress = progress,
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "  ${(progress * 100).toInt()}%",
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    }

}

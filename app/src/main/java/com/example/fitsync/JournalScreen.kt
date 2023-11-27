package com.example.fitsync

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable function for displaying the Journal screen.
 * It shows a performance chart based on the user's past week's step counts and other relevant data.
 */
@Composable
fun JournalScreen() {
    val pastWeek = listOf(2000f, 1200f, 900f, 1100f, 2200f, 2100f, 1900f)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Past Week",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .padding(5.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ){
            PerformanceChart(modifier = Modifier
                .height(220.dp)
                .padding(16.dp), list = pastWeek)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxHeight().fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "there will be other things in here")
//            ArcComposable(Modifier)
        }
    }

}

/**
 * Composable function for rendering a circular progress indicator (arc).
 * @param modifier Modifier passed to customize layout or appearance.
 */
@Composable
private fun ArcComposable(modifier: Modifier) {
    Box(
        modifier = modifier.padding(5.dp)
    ) {
        Canvas(modifier = Modifier
            .size(200.dp)) {
            drawArc(
                color = Color.LightGray,
                -180f,
                360f,
                useCenter = false,
                size = Size(size.width, size.height),
                style = Stroke(8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = "50%",
            color = Color.White,
            fontSize = 20.sp
        )
    }
}

/**
 * Composable function for drawing a performance chart.
 * The chart displays a graphical representation of step counts or other metrics over time.
 * @param modifier Modifier passed to customize layout or appearance.
 * @param list A list of Float values representing the data points to be plotted on the chart.
 */
@Composable
fun PerformanceChart(modifier: Modifier = Modifier, list: List<Float>) {
    val zipList: List<Pair<Float, Float>> = list.zipWithNext()

    Row(modifier = modifier) {
        val max = list.max()
        val min = list.min()

        val lineColor = Color.Green

        for (pair in zipList) {

            val fromValuePercentage = getRelativeValue(pair.first, max, min)
            val toValuePercentage = getRelativeValue(pair.second, max, min)

            Canvas(
                modifier = Modifier.fillMaxHeight().weight(1f)
                    .border(width = 1.dp, color = Color.White.copy(alpha = 0.3f), ),
                onDraw = {
                    val fromPoint = Offset(x = 0f, y = size.height.times(1 - fromValuePercentage)) // <-- Use times so it works for any available space
                    val toPoint = Offset(x = size.width, y = size.height.times((1 - toValuePercentage))) // <-- Also here!

                    drawLine(color = lineColor, start = fromPoint, end = toPoint, strokeWidth = 3f)
                })
        }
    }
}

/**
 * Calculates the relative value of a data point in the range between the maximum and minimum values.
 * This is used for plotting the data points on the performance chart.
 * @param value The data point value to be normalized.
 * @param max The maximum value in the data set.
 * @param min The minimum value in the data set.
 * @return The relative value (normalized) of the data point.
 */
private fun getRelativeValue(value: Float, max: Float, min: Float) =
    (value - min) / (max - min)

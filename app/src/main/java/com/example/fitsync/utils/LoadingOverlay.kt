package com.example.fitsync.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

// Composable function for displaying a loading overlay.
@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize().zIndex(10f)
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            strokeWidth = 5.dp,
            modifier = Modifier.height(30.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}
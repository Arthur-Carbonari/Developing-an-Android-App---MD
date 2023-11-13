package com.example.fitsync

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(Modifier.height(1.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Your Name", style = MaterialTheme.typography.titleLarge)
            Text(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Column(
            modifier = Modifier.fillMaxHeight(0.75f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            // Social media icons could be horizontally laid out here, if needed.
            ProfileOptionItem(title = "User", icon = Icons.Filled.Person)
            ProfileOptionItem(title = "Health", icon = Icons.Filled.Favorite)
            ProfileOptionItem(title = "Account Settings", icon = Icons.Filled.Settings)
        }


    }
}

@Composable
fun ProfileOptionItem(title: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {},
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Go to $title"
            )
        }
    }
}
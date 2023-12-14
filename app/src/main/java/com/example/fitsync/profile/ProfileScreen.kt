package com.example.fitsync.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.fitsync.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable function for displaying the Profile screen.
 * It shows the user's profile picture, name, and other profile options.
 */
@Composable
fun ProfileScreen() {
    val user = FirebaseAuth.getInstance().currentUser
    val userName = user?.displayName ?: "Guest"
    val profilePhotoUrl = user?.photoUrl
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
                painter = // Fallback image on error
                rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = profilePhotoUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                            error(R.drawable.ic_launcher_background) // Fallback image on error
                        }).build()
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(userName, style = MaterialTheme.typography.titleLarge)
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
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                    FirebaseAuth.getInstance().signOut()
                },
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Logout", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.weight(1f))
//                    Icon(
//                        imageVector = Icons.Filled.ArrowForward,
//                        contentDescription = "Go to $title"
//                    )
                }
            }

        }


    }
}

/**
 * Composable function for rendering individual profile option items.
 * Each item is displayed as a card with an icon and title.
 * @param title The title of the profile option.
 * @param icon The icon representing the profile option.
 */
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
package com.example.fitsync

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.Task

/**
 * Composable function for the main screen of the app.
 * It sets up the navigation controller and manages navigation to different screens.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { if(showBottomBar) MyNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            NavHost(navController, startDestination = AppRoutes.HOME.name) {
//                composable(AppRoutes.REGISTRATION.name) { RegistrationScreen(navController, onSignInClick) }
                composable(AppRoutes.HOME.name) { HomeScreen() }
                composable(AppRoutes.JOURNAL.name) { JournalScreen() }
                composable(AppRoutes.PROFILE.name) { ProfileScreen() }
            }
        }
    }

    // Listen for navigation changes to toggle the visibility of the bottom bar
    navController.addOnDestinationChangedListener { _, destination, _ ->
        showBottomBar = when (destination.route) {
            AppRoutes.REGISTRATION.name -> false
            else -> true
        }
    }
}


/**
 * Data class representing a navigation item in the bottom navigation bar.
 * It holds the route, title, and icons for the navigation item.
 * @param route The route associated with the navigation item.
 * @param title The title of the navigation item.
 * @param outlinedIcon The icon used when the item is not selected.
 * @param filledIcon The icon used when the item is selected.
 */
data class NavigationItem(
    val route: AppRoutes,
    val title: String,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector
)


/**
 * Composable function for the bottom navigation bar.
 * It displays navigation items and handles navigation actions.
 * @param navController The NavController to manage app navigation.
 */
@Composable
fun MyNavigationBar(navController: NavController) {
    val navigationItems = listOf(
        NavigationItem(AppRoutes.HOME, "Home", Icons.Outlined.Home, Icons.Filled.Home),
        NavigationItem(AppRoutes.JOURNAL, "Journal", Icons.Outlined.DateRange, Icons.Filled.DateRange),
        NavigationItem(AppRoutes.PROFILE, "Profile", Icons.Outlined.Person, Icons.Filled.Person)
    )

    var selectedItem by remember { mutableStateOf(AppRoutes.HOME) } // Remembering the selected item

    NavigationBar(
        containerColor = Color(0xFF272429)
    ) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == item.route) item.filledIcon else item.outlinedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(item.title) },
                selected = selectedItem == item.route,
                onClick = {
                    selectedItem = item.route // Updating the selected item
                    navController.navigate(item.route.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

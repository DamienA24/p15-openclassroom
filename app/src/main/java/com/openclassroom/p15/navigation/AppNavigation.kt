package com.openclassroom.p15.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassroom.p15.ui.screens.CreateEventScreen
import com.openclassroom.p15.ui.screens.EventDetailScreen
import com.openclassroom.p15.ui.screens.EventListScreen
import com.openclassroom.p15.ui.screens.LoginScreen
import com.openclassroom.p15.ui.screens.ProfileScreen
import com.openclassroom.p15.ui.viewmodel.AuthViewModel
import com.openclassroom.p15.ui.viewmodel.EventViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object EventList : Screen("event_list")
    object Profile : Screen("profile")
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object CreateEvent : Screen("create_event")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            // EventViewModel scopé au NavBackStackEntry de Home,
            // partagé ensuite avec CreateEvent via getBackStackEntry
            val eventViewModel: EventViewModel = hiltViewModel()
            MainLayout(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEventClick = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                eventViewModel = eventViewModel
            )
        }

        composable(Screen.CreateEvent.route) {
            // Récupère le même EventViewModel que celui de Home
            val homeEntry = remember(navController) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val eventViewModel: EventViewModel = hiltViewModel(homeEntry)
            CreateEventScreen(
                onBack = { navController.popBackStack() },
                onEventCreated = {
                    eventViewModel.loadEvents()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.EventDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            EventDetailScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainLayout(
    onLogout: () -> Unit,
    onEventClick: (String) -> Unit = {},
    onCreateEvent: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Screen.EventList, "Events", Icons.Default.DateRange),
        BottomNavItem(Screen.Profile, "Profile", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            innerNavController.navigate(item.screen.route) {
                                popUpTo(Screen.EventList.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = innerNavController,
            startDestination = Screen.EventList.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.EventList.route) {
                EventListScreen(
                    onCreateEvent = onCreateEvent,
                    onEventClick = onEventClick,
                    eventViewModel = eventViewModel
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        authViewModel.signOut(context) { onLogout() }
                    }
                )
            }
        }
    }
}

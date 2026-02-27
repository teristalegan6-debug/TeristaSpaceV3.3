package com.terista.space.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.terista.space.ui.screens.HomeScreen
import com.terista.space.ui.screens.VirtualAppsScreen
import com.terista.space.ui.screens.WorkProfileScreen
import com.terista.space.ui.screens.DeviceInfoScreen
import com.terista.space.ui.screens.SettingsScreen

@Composable
fun TeristaNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "home"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToVirtualApps = { navController.navigate("virtual_apps") },
                onNavigateToWorkProfile = { navController.navigate("work_profile") },
                onNavigateToDeviceInfo = { navController.navigate("device_info") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("virtual_apps") {
            VirtualAppsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("work_profile") {
            WorkProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("device_info") {
            DeviceInfoScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

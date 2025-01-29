package eurowag.assignment.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eurowag.assignment.ui.main.MainScreen
import eurowag.assignment.ui.settings.SettingsScreen
import eurowag.assignment.ui.statistics.StatScreen

@Composable
fun MainCompose(
    navHostController: NavHostController,
    permissionRequest: () -> Unit,
) {
    Box(modifier = Modifier) {
        NavHost(
            navController = navHostController,
            startDestination = Screen.Main.route
        ) {
            composable(route = Screen.Main.route) {
                MainScreen(
                    navController = navHostController,
                    permissionRequest = permissionRequest
                )
            }
            composable(route = Screen.Settings.route) {
                SettingsScreen(navController = navHostController)
            }
            composable(route = Screen.Stat.route) {
                StatScreen(navController = navHostController)
            }
        }
    }
}


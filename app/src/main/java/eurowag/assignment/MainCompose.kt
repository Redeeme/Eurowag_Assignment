package eurowag.assignment

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eurowag.assignment.ui.main.MainScreen
import eurowag.assignment.ui.MainViewModel
import eurowag.assignment.ui.settings.SettingsScreen
import eurowag.assignment.ui.statistics.StatScreen
import eurowag.assignment.ui.navigation.Screen
import eurowag.assignment.ui.statistics.StatisticsViewModel

@Composable
fun MainCompose(
    navHostController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(),
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
                    viewModel = mainViewModel,
                    permissionRequest = permissionRequest
                )
            }
            composable(route = Screen.Settings.route) {
                SettingsScreen(navController = navHostController, mainViewModel = mainViewModel)
            }
            composable(route = Screen.Stat.route) {
                val viewModel: StatisticsViewModel = hiltViewModel()
                StatScreen(navController = navHostController, viewModel = viewModel)
            }

        }
    }
}


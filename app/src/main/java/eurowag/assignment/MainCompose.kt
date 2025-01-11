package eurowag.assignment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import eurowag.assignment.layouts.MainScreen
import eurowag.assignment.layouts.SettingsScreen
import eurowag.assignment.layouts.StatScreen
import eurowag.assignment.layouts.navigation.Screen

@Composable
fun MainCompose(
    navHostController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Box(modifier = Modifier) {
        NavHost(
            navController = navHostController,
            startDestination = Screen.Main.route
        ) {
            composable(route = Screen.Main.route) {
                MainScreen(navController = navHostController, viewModel = mainViewModel)
            }
            composable(route = Screen.Settings.route) {
                SettingsScreen(navController = navHostController, mainViewModel = mainViewModel)
            }
            composable(route = Screen.Stat.route) {
                StatScreen(navController = navHostController)
            }

        }
    }
}


package eurowag.assignment.layouts.navigation

sealed class Screen(val route: String) {
    object Main: Screen(route = "main_screen")
    object Settings: Screen(route = "settings_Screen")
    object Stat: Screen(route = "stat_Screen")
}
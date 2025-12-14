package dev.imranr.obtainium.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.imranr.obtainium.ui.screens.*
import dev.imranr.obtainium.viewmodel.AppsViewModel
import dev.imranr.obtainium.viewmodel.SettingsViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Apps : Screen("apps", "Apps", Icons.Default.Apps)
    object AddApp : Screen("add_app", "Add App", Icons.Default.Add)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object AppDetail : Screen("app_detail/{appId}", "App Details", Icons.Default.Info)
    object ImportExport : Screen("import_export", "Import/Export", Icons.Default.ImportExport)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObtainiumApp(
    navController: NavHostController = rememberNavController(),
    appsViewModel: AppsViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when (currentRoute) {
                            Screen.Apps.route -> Screen.Apps.title
                            Screen.AddApp.route -> Screen.AddApp.title
                            Screen.Settings.route -> Screen.Settings.title
                            Screen.ImportExport.route -> Screen.ImportExport.title
                            else -> "Obtainium"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    if (currentRoute != Screen.Apps.route) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute == Screen.Apps.route || 
                currentRoute == Screen.AddApp.route || 
                currentRoute == Screen.Settings.route) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Screen.Apps.icon, contentDescription = Screen.Apps.title) },
                        label = { Text(Screen.Apps.title) },
                        selected = currentRoute == Screen.Apps.route,
                        onClick = {
                            if (currentRoute != Screen.Apps.route) {
                                navController.navigate(Screen.Apps.route) {
                                    popUpTo(Screen.Apps.route) { inclusive = true }
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Screen.AddApp.icon, contentDescription = Screen.AddApp.title) },
                        label = { Text(Screen.AddApp.title) },
                        selected = currentRoute == Screen.AddApp.route,
                        onClick = {
                            if (currentRoute != Screen.AddApp.route) {
                                navController.navigate(Screen.AddApp.route)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Screen.Settings.icon, contentDescription = Screen.Settings.title) },
                        label = { Text(Screen.Settings.title) },
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            if (currentRoute != Screen.Settings.route) {
                                navController.navigate(Screen.Settings.route)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Apps.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Apps.route) {
                AppsScreen(
                    viewModel = appsViewModel,
                    onAppClick = { appId ->
                        navController.navigate("app_detail/$appId")
                    },
                    onNavigateToImportExport = {
                        navController.navigate(Screen.ImportExport.route)
                    }
                )
            }
            
            composable(Screen.AddApp.route) {
                AddAppScreen(
                    viewModel = appsViewModel,
                    onAppAdded = {
                        navController.navigateUp()
                    }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel
                )
            }
            
            composable("app_detail/{appId}") { backStackEntry ->
                val appId = backStackEntry.arguments?.getString("appId") ?: return@composable
                AppDetailScreen(
                    appId = appId,
                    viewModel = appsViewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            
            composable(Screen.ImportExport.route) {
                ImportExportScreen(
                    viewModel = appsViewModel
                )
            }
        }
    }
}

package com.jeansock.manager.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jeansock.manager.data.JinRepository
import com.jeansock.manager.ui.screens.AddJinScreen
import com.jeansock.manager.ui.screens.SettingsScreen
import com.jeansock.manager.ui.screens.SplashScreen
import com.jeansock.manager.ui.screens.StartScreen

object Routes {
    const val SPLASH = "splash"
    const val START = "start"
    const val ADD_JIN = "add_jin"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph() {
    val context = LocalContext.current
    val repository = remember { JinRepository(context) }
    val navController = rememberNavController()

    var entries by remember { mutableStateOf(repository.getEntries()) }
    var pricePerDozen by remember { mutableLongStateOf(repository.getPricePerDozen()) }

    val stats = remember(entries) {
        repository.computeStats(entries)
    }

    val goHome: () -> Unit = {
        navController.navigate(Routes.START) {
            popUpTo(Routes.START) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.START) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.START) {
            StartScreen(
                entries = entries,
                stats = stats,
                onAddClick = {
                    navController.navigate(Routes.ADD_JIN) {
                        launchSingleTop = true
                    }
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS) {
                        launchSingleTop = true
                    }
                },
                onDeleteEntry = { entryId ->
                    entries = repository.deleteEntry(entryId)
                }
            )
        }

        composable(Routes.ADD_JIN) {
            BackHandler {
                goHome()
            }
            AddJinScreen(
                onAddConfirmed = { dozenCount, selectedDate ->
                    entries = repository.addEntry(dozenCount, selectedDate)
                    goHome()
                },
                onBackToHome = goHome,
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.SETTINGS) {
            BackHandler {
                goHome()
            }
            SettingsScreen(
                currentPricePerDozen = pricePerDozen,
                repository = repository,
                onSavePrice = { newPrice ->
                    repository.setPricePerDozen(newPrice)
                    pricePerDozen = repository.getPricePerDozen()
                },
                onBackupRestored = {
                    pricePerDozen = repository.getPricePerDozen()
                    entries = repository.getEntries()
                },
                onAddClick = {
                    navController.navigate(Routes.ADD_JIN) {
                        launchSingleTop = true
                    }
                },
                onBackToHome = goHome
            )
        }
    }
}

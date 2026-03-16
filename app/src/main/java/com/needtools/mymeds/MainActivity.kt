package com.needtools.mymeds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.needtools.mymeds.db.PillDatabase
import com.needtools.mymeds.screen.AddScreen
import com.needtools.mymeds.screen.EditScreen
import com.needtools.mymeds.screen.EmptyMedsScreen
import com.needtools.mymeds.screen.HistoryScreen
import com.needtools.mymeds.screen.InfoScreen
import com.needtools.mymeds.screen.SplashScreen
import com.needtools.mymeds.screen.MedsScreen
import com.needtools.mymeds.screen.SettingsScreen
import com.needtools.mymeds.util.MedsViewModel
import com.needtools.mymeds.util.MedsViewModelFactory
import com.needtools.mymeds.util.SettingsManager


class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = PillDatabase.getDatabase(applicationContext)
        val pillDao = database.pillDao()
        val intakeDao = database.pillIntakeDao()
        val medsViewModel: MedsViewModel by viewModels {
            MedsViewModelFactory(pillDao, intakeDao, settingsManager)
        }

        setContent {

            settingsManager = SettingsManager(this)
            MaterialTheme {

                val navController = rememberNavController()

                val pills by pillDao.getAllPills().collectAsState(initial = emptyList())

                NavHost(
                    navController = navController,
                    startDestination = "splash"

                ) {
                    composable("splash") { SplashScreen(navController) }
                    composable("meds") {
                        if (pills.isEmpty()) {
                            EmptyMedsScreen(
                                onAddPillClick = { navController.navigate("add") },
                                onNavigateToInfo = { navController.navigate("info") }
                            )
                        } else {
                            MedsScreen(
                                viewModel = medsViewModel,
                                onAddPillClick = {
                                    navController.navigate("add")
                                },
                                navController = navController
                            )
                        }
                    }
                    composable("settings") { SettingsScreen(navController,  settingsManager) }
                    composable("info") { InfoScreen(navController) }

                    composable("add") {
                        AddScreen(
                        onPillSaved = { newPill ->
                            medsViewModel.savePill(newPill)
                    },
                        onBack = { navController.popBackStack() }
                        )
                    }
                    composable("edit/{pillId}") { backStackEntry ->
                        val pillId = backStackEntry.arguments?.getString("pillId")?.toIntOrNull()
                        EditScreen(
                            pillId = pillId,
                            navController = navController,
                            onBack = { navController.navigateUp() },
                            viewModel = medsViewModel
                        )
                    }
                    composable("history/{pillId}") { backStackEntry ->
                        val pillId = backStackEntry.arguments?.getString("pillId")?.toIntOrNull()
                        HistoryScreen(
                            pillId = pillId ?: 0,
                            viewModel = medsViewModel,
                            onBack = { navController.navigateUp() },
                        )
                    }

                }
            }
        }
    }
}

package com.mcwilliams.memerator

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mcwilliams.memerator.ui.MemeratorContent
import com.mcwilliams.memerator.ui.MemeratorViewModel
import com.mcwilliams.memerator.ui.theme.MemeratorTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalUnitApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Keep
@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : ComponentActivity() {
    private val viewModel: MemeratorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val items = listOf(
                NavigationDestination.StravaDashboard,
            )

            MemeratorTheme {
                Scaffold(
                    content = { paddingValues ->
                        NavHost(
                            navController,
                            startDestination = NavigationDestination.StravaDashboard.destination
                        ) {
                            composable(NavigationDestination.StravaDashboard.destination) {
                                MemeratorContent(
                                    viewModel = viewModel,
                                    paddingValues = paddingValues
                                )
                            }
//                                    composable(NavigationDestination.StreakSettings.destination) {
//                                        StreakSettingsView(
//                                            viewModel = viewModel,
//                                            paddingValues = paddingValues
//                                        )
//                                    }
                        }
                    },
                )
            }
        }
    }
}

@Keep
sealed class NavigationDestination(
    val destination: String,
    val label: String? = null,
    val resId: Int? = null,
) {
    object StravaDashboard :
        NavigationDestination("stravaDashboard", "Dashboard", R.drawable.ic_dash)
}
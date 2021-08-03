package com.mcwilliams.memerator

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mcwilliams.memerator.ui.dashboard.StravaDashboard
import com.mcwilliams.memerator.ui.dashboard.MemeratorViewModel
import com.mcwilliams.memerator.ui.theme.MemeratorTheme
import dagger.hilt.android.AndroidEntryPoint

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
                                StravaDashboard(
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
//                            bottomBar = {
//                                BottomNavigation(
//                                    elevation = 16.dp,
//                                    backgroundColor = primaryBlueShade2
//                                ) {
//                                    val navBackStackEntry by navController.currentBackStackEntryAsState()
//                                    val currentRoute = navBackStackEntry?.destination?.route
//                                    items.forEach { screen ->
//                                        BottomNavigationItem(
//                                            icon = {
//                                                Icon(
//                                                    painter = painterResource(id = screen.resId!!),
//                                                    contentDescription = "",
//                                                    modifier = Modifier.size(24.dp)
//                                                )
////                                            val animationSpec =
////                                                remember { LottieAnimationSpec.RawRes(screen.resId!!) }
////
////                                            LottieAnimation(
////                                                animationSpec,
////                                                modifier = Modifier.size(24.dp)
////                                            )
//                                            },
//                                            label = { Text(screen.label!!) },
//                                            selected = currentRoute == screen.destination,
//                                            onClick = {
//                                                navController.navigate(screen.destination) {
//                                                    // Pop up to the start destination of the graph to
//                                                    // avoid building up a large stack of destinations
//                                                    // on the back stack as users select items
//                                                    popUpTo(navController.graph.startDestinationRoute!!) {
//                                                        saveState = true
//                                                    }
//                                                    // Avoid multiple copies of the same destination when
//                                                    // reselecting the same item
//                                                    launchSingleTop = true
//                                                    restoreState = true
//                                                }
//                                            }
//                                        )
//                                    }
//                                }
//                            }
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

//    object StreakSettings :
//        NavigationDestination("streakSettings", "Settings", R.drawable.ic_settings)
}
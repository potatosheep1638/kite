package com.potatosheep.kite.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.potatosheep.kite.app.nav.TopLevelDestination
import com.potatosheep.kite.app.nav.navigateToTopLevel

@Composable
fun rememberAppState(
    shouldShowOnboarding: Boolean,
    navController: NavHostController = rememberNavController(),
): KiteAppState {
    return remember(
        navController
    ) {
        KiteAppState(
            shouldShowOnboarding = shouldShowOnboarding,
            navController = navController,
        )
    }
}

class KiteAppState(
    val shouldShowOnboarding: Boolean,
    val navController: NavHostController,
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    fun navigateToTopLevelDestination(popFirstDestination: Boolean) {
        trace("Navigation: Top Level") {
            val topLevelNavOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = popFirstDestination
                    saveState = true
                }

                launchSingleTop = true
                restoreState = true
            }

            navController.navigateToTopLevel(topLevelNavOptions)
        }
    }
}

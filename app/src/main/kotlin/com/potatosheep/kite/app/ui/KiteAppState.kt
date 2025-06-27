package com.potatosheep.kite.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    //val currentNavDestination: NavDestination?
    //    @Composable get() = currentDestination?.hierarchy?.first()

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }

                launchSingleTop = true
                restoreState = true
            }

            navController.navigateToTopLevel(topLevelNavOptions)
        }
    }
}

private const val TOP_LEVEL_DEST = "topLevelDest"

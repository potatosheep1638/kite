package com.potatosheep.kite.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.potatosheep.kite.app.nav.TopLevelDestination
import com.potatosheep.kite.app.nav.TopLevelDestination.FEED
import com.potatosheep.kite.app.nav.TopLevelDestination.LIBRARY
import com.potatosheep.kite.core.data.repo.UserConfigRepository
import com.potatosheep.kite.feature.homefeed.nav.navigateToFeed
import com.potatosheep.kite.feature.library.nav.navigateToLibrary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberAppState(
    userConfigRepository: UserConfigRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): KiteAppState {
    return remember(
        navController
    ) {
        KiteAppState(
            userConfigRepository = userConfigRepository,
            coroutineScope = coroutineScope,
            navController = navController,
        )
    }
}

class KiteAppState(
    val userConfigRepository: UserConfigRepository,
    val coroutineScope: CoroutineScope,
    val navController: NavHostController,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentNavDestination: NavDestination?
        @Composable get() = currentDestination?.hierarchy?.first()

    val shouldShowOnboarding = userConfigRepository.userConfig
        .map { !it.shouldHideOnboarding }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

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

            when (topLevelDestination) {
                FEED -> navController.navigateToFeed(topLevelNavOptions)
                LIBRARY -> navController.navigateToLibrary(topLevelNavOptions)
            }
        }
    }
}
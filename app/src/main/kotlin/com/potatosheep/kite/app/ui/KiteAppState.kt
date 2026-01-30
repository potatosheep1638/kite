package com.potatosheep.kite.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.potatosheep.kite.app.navigation.TopLevelDestination
import com.potatosheep.kite.app.navigation.TopLevelNavKey
import com.potatosheep.kite.core.navigation.NavigationState
import com.potatosheep.kite.core.navigation.rememberNavigationState
import com.potatosheep.kite.feature.feed.api.navigation.FeedNavKey
import com.potatosheep.kite.feature.onboarding.api.navigation.OnboardingNavKey

@Composable
fun rememberAppState(
    shouldShowOnboarding: Boolean,
    navController: NavHostController = rememberNavController(),
): KiteAppState {
    val startRoute = if (shouldShowOnboarding) OnboardingNavKey else FeedNavKey
    val navigationState = rememberNavigationState(startRoute, setOf(TopLevelNavKey));

    return remember(
        navController
    ) {
        KiteAppState(
            shouldShowOnboarding = shouldShowOnboarding,
            navController = navController,
            navigationState = navigationState
        )
    }
}

class KiteAppState(
    val shouldShowOnboarding: Boolean,
    val navController: NavHostController,
    val navigationState: NavigationState
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
}

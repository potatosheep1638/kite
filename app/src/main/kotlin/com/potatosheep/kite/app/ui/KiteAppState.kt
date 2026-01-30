package com.potatosheep.kite.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.potatosheep.kite.app.navigation.TopLevelDestination
import com.potatosheep.kite.app.navigation.TopLevelNavKey
import com.potatosheep.kite.core.navigation.NavigationState
import com.potatosheep.kite.core.navigation.rememberNavigationState
import com.potatosheep.kite.feature.onboarding.api.navigation.OnboardingNavKey

@Composable
fun rememberAppState(
    shouldShowOnboarding: Boolean,
): KiteAppState {
    val startRoute = if (shouldShowOnboarding) OnboardingNavKey else TopLevelNavKey
    val navigationState = rememberNavigationState(startRoute, setOf(TopLevelNavKey));

    return remember {
        KiteAppState(
            shouldShowOnboarding = shouldShowOnboarding,
            navigationState = navigationState
        )
    }
}

class KiteAppState(
    val shouldShowOnboarding: Boolean,
    val navigationState: NavigationState
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
}

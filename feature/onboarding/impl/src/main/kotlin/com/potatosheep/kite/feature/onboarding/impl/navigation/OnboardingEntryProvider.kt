package com.potatosheep.kite.feature.onboarding.impl.navigation

import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.onboarding.api.navigation.OnboardingNavKey
import com.potatosheep.kite.feature.onboarding.impl.OnboardingRoute
import com.potatosheep.kite.feature.onboarding.impl.OnboardingViewModel
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

fun NavController.navigateToOnboarding(navOptions: NavOptions? = null) =
    navigate(
        OnboardingRoute,
        navOptions
    )

fun NavGraphBuilder.onboardingScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) = composable<OnboardingRoute> {
    OnboardingRoute(
        onBackClick = onBackClick,
        onNextClick = onNextClick,
        modifier = modifier
    )
}

fun EntryProviderScope<NavKey>.onboardingEntry(navigator: Navigator, onNextClick: () -> Unit) {
    entry<OnboardingNavKey> {
        OnboardingRoute(
            onBackClick = { navigator.goBack() },
            onNextClick = onNextClick,
        )
    }
}

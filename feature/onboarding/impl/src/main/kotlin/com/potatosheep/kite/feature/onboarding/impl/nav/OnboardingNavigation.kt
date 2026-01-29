package com.potatosheep.kite.feature.onboarding.impl.nav

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.potatosheep.kite.feature.onboarding.impl.OnboardingRoute
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
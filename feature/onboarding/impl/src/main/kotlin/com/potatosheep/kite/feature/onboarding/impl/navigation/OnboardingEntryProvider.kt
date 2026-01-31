package com.potatosheep.kite.feature.onboarding.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.onboarding.api.navigation.OnboardingNavKey
import com.potatosheep.kite.feature.onboarding.impl.OnboardingRoute
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

fun EntryProviderScope<NavKey>.onboardingEntry(navigator: Navigator, onNextClick: () -> Unit) {
    entry<OnboardingNavKey>(metadata = defaultTransitionSpec()) {
        OnboardingRoute(
            onBackClick = { navigator.goBack() },
            onNextClick = onNextClick,
        )
    }
}

package com.potatosheep.kite.feature.onboarding.api.navigation

import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
object OnboardingNavKey : NavKey

fun Navigator.navigateToOnboarding() {
    navigate(OnboardingNavKey)
}
package com.potatosheep.kite.app.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.potatosheep.kite.app.nav.KiteNavHost
import com.potatosheep.kite.app.nav.TopLevelRoute
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.feature.onboarding.nav.OnboardingRoute

@Composable
fun KiteApp(
    appState: KiteAppState,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = LocalBackgroundColor.current,
        modifier = modifier
    ) {
        val destination: Any = if (appState.shouldShowOnboarding) {
            OnboardingRoute
        } else {
            TopLevelRoute
        }

        KiteNavHost(
            appState = appState,
            startDestination = destination,
            modifier = modifier,
        )
    }
}
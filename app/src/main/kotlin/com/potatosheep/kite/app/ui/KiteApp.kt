package com.potatosheep.kite.app.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.app.nav.KiteNavHost
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.feature.homefeed.nav.FeedRoute
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
        val shouldShowOnboarding by appState.shouldShowOnboarding.collectAsStateWithLifecycle()

        val destination = if (shouldShowOnboarding) {
            OnboardingRoute
        } else {
            FeedRoute
        }

        KiteNavHost(
            appState = appState,
            startDestination = destination,
            modifier = modifier,
        )
    }
}